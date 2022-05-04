package com.example.demo.repository;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
	
	private final EntityManager entityManager;

	public User findOne(Long id) {
		return entityManager.find(User.class, id);
	}
}
