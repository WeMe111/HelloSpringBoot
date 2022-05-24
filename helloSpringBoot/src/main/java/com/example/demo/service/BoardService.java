package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.FileEntity;
import com.example.demo.domain.dto.board.BoardDeleteRequestDto;
import com.example.demo.domain.dto.board.BoardSaveRequestDto;
import com.example.demo.domain.dto.board.BoardUpdateRequestDto;
import com.example.demo.domain.user.User;
import com.example.demo.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    //글작성 로직
//    @Transactional
//    public Long save(BoardSaveRequestDto boardSaveRequestDto, User user) {
//        boardSaveRequestDto.setUser(user);
//        return boardRepository.save(boardSaveRequestDto.toEntity()).getId();
//    }
    @Transactional
    public Long register(BoardSaveRequestDto boardSaveRequestDto, User user) {
    	boardSaveRequestDto.setUser(user);
    	return boardRepository.save(boardSaveRequestDto.toEntity()).getId();
    }
    
    //글목록 로직
//    @Transactional(readOnly = true)
//    public Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable) {
//        return boardRepository.findByTitleContainingOrContentContaining(title, content, pageable);
//    }
    @Transactional(readOnly = true)
	public Page<Board> selectList(Pageable pageable, String select, String keyword) {
		
		String useYn = "Y";
		
		if(select.equals("title")) {
			return boardRepository.findByTitleContainingAndUseYnIgnoreCase(keyword, useYn, pageable);
		} else if(select.equals("content")) {
			return boardRepository.findByContentContainingAndUseYnIgnoreCase(keyword, useYn, pageable);
		} else {
			return boardRepository.findByUseYn(useYn, pageable);
		}
		
	}
    
    //글상세 로직
    @Transactional(readOnly = true)
    public Board detail(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다. id=" + id));
    }
    
    //글삭제 로직
//    @Transactional
//    public void deleteById(Long id) {
//        boardRepository.deleteById(id);
//    }
    @Transactional
    public Long delete(Long id, BoardDeleteRequestDto boardDeleteRequestDto) {
    	Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다." + id));
    	board.delete(boardDeleteRequestDto.getUseYn());
        return id;
    }

    //글수정 로직
   @Transactional
   public Long update(Long id, BoardUpdateRequestDto boardUpdateRequestDto) {
       Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id가 없습니다. id=" + id));
       board.update(boardUpdateRequestDto.getTitle(), boardUpdateRequestDto.getContent());
       return id;
   }
    
   //글 조회수 로직
   @Transactional
   public int updateCount(Long id) {
       return boardRepository.updateCount(id);
   }
   
}