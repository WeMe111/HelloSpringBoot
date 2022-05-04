package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.User;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final MemberRepository memberRepository;
	
	public List<User> findUser(){
		return userRepository.findAll();
	}
	
	public User findOne(Long id) {
		return memberRepository.findOne(id);
	}
	
	@Transactional
	public void userUpdate(User user) {
		userRepository.save(user);
	}
	
}
