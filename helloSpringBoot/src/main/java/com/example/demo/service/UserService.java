package com.example.demo.service;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public List<User> findUser(){
		return userRepository.findAll();
	}
	
	//회원 수정
	@Transactional
	public Long Update(User user, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		User userEntity = userRepository.findById(user.getIdu()).orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + user.getIdu()));
        userEntity.update(bCryptPasswordEncoder.encode(user.getPassword()), user.getName());
        principalDetails.setUser(userEntity);
        return userEntity.getIdu();
	}

	
}
