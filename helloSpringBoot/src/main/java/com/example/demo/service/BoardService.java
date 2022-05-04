package com.example.demo.service;

import java.util.List;

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
	
	public Long register(BoardDto boardDto) {
		return boardRepository.save(boardDto.toEntity()).getIdb();
	}
	
	public List<Board> list(){
		return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "idb"));
	}
	
}
