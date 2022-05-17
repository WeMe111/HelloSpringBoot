package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.reply.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

}
