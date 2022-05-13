package com.example.demo.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Board;
import com.example.demo.domain.Reply;
import com.example.demo.domain.User;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.ReplyRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReplyService {

	private final ReplyRepository replyRepository;
	private final BoardRepository boardRepository;
	
	@Transactional
    public void replySave(Long idb, Reply reply, User user) {
        Board board = boardRepository.findById(idb).orElseThrow(() -> new IllegalArgumentException("해당 boardId가 없습니다. id=" + idb));

        reply.save(board, user);

        replyRepository.save(reply);
    }
}
