package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	// findBy 규칙 -> UserId 문법
	// ex) select * from user where userId = ?
	public User findByUsername(String username);

}
