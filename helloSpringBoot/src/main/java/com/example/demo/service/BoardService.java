package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Board;
import com.example.demo.domain.BoardFile;
import com.example.demo.domain.dto.BoardDto;
import com.example.demo.repository.BoardFileRepository;
import com.example.demo.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	private final BoardFileRepository boardFileRepository;
	
	public Long savePost(Board board) {
		return boardRepository.save(board).getIdb();
	}
	
	public Optional<Board> find(Long idb) {
		return this.boardRepository.findById(idb);
	}
	
	public Page<Board> search(String title, String content, Pageable pageable){
		return boardRepository.findByTitleContainingOrContentContaining(title, content, pageable);
	}
	
	@Transactional
	public BoardDto getPost(Long idb) {
		Optional<Board> boardWeapper = boardRepository.findById(idb);
		Board board = boardWeapper.get();
		
		BoardDto boardDto = BoardDto.builder()
				.idb(board.getIdb())
				.title(board.getTitle())
				.content(board.getContent())
				.Writer(board.getWriter())
				.build();
		
		return boardDto;
	}
	
	 //게시판 파일 등록
    @Transactional
    public void insertBoardFile(Board board) {
        //파일 등록할게 있을경우만
        if(board.getFileIdxs() != null) {
            //파일 등록
            String fileIdxs = ((String) board.getFileIdxs()).replace("[", "").replace("]", "");
            String[] fileIdxArray = fileIdxs.split(",");

            for (int i=0; i<fileIdxArray.length; i++) {
                String fileIdx = fileIdxArray[i].trim();
                BoardFile boardFile = new BoardFile(board.getIdb(), Long.parseLong(fileIdx),"Y") ;
                boardFileRepository.save(boardFile);
            }
        }
    }
    
    public List<BoardFile> selectBoardFile(Long boardIdx) {
    	return boardFileRepository.findByBoardIdx(boardIdx);
    }
	
//	@Transactional
//	public void deletePost(Long idb) {
//		boardRepository.deleteById(idb);
//	}
	public void deleteBoard(List<String> boardIdbArray){
        for(int i=0; i<boardIdbArray.size(); i++) {
            String boardIdb = boardIdbArray.get(i);
            Optional<Board> optional = boardRepository.findById(Long.parseLong(boardIdb));
            if(optional.isPresent()){
                Board board = optional.get();
                board.setUseYn("N");
                boardRepository.save(board);
            }
            else{
                throw new NullPointerException();
            }
        }
    }
	
	public int updateCount(Long idb) {
		return boardRepository.updateCount(idb);
	}

}
