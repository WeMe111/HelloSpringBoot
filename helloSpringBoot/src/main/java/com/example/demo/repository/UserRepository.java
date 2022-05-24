package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	// findBy 규칙 -> UserId 문법
	// ex) select * from user where userId = ?
	Optional<User> findByUsername(String username);
}
