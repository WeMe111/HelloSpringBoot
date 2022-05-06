package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Board;
import com.example.demo.domain.dto.BoardDto;
import com.example.demo.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;
	
	public Long savePost(BoardDto boardDto) {
		return boardRepository.save(boardDto.toEntity()).getIdb();
	}
	
	public List<Board> list(){
		return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "idb"));
	}
	
	@Transactional
	public BoardDto getPost(Long idb) {
		Optional<Board> boardWeapper = boardRepository.findById(idb);
		Board board = boardWeapper.get();
		
		BoardDto boardDto = BoardDto.builder()
				.idb(board.getIdb())
				.title(board.getTitle())
				.content(board.getContent())
				.writer(board.getWriter())
				.build();
		
		return boardDto;
	}
	
	@Transactional
	public void deletePost(Long idb) {
		boardRepository.deleteById(idb);
	}
	
}
