package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	
	
	@GetMapping("/user/home")
	public String home() {
		return "user/home";
	}
	
	//회원 정보 수정 페이지
	@GetMapping("/user/update")
	public String userUpdate() {
		return "user/update";
	}
	

}
