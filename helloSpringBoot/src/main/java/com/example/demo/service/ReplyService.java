package com.example.demo.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.reply.Reply;
import com.example.demo.domain.user.User;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.ReplyRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void replySave(Long boardId, Reply reply, User user) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("해당 boardId가 없습니다. id=" + boardId));

        reply.save(board, user);

        replyRepository.save(reply);
    }
    
    @Transactional
    public void replyDelete(Long replyId) {
        replyRepository.deleteById(replyId);
    }
}
