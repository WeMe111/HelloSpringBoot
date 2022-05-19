package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}

	//회원가입 페이지 이동
	@GetMapping("/auth/signup")
    public String userSave() {
        return "signup";
    }
	
	//로그인 페이지 이동
	@GetMapping("/auth/login")
    public String userLogin() {
        return "login";
    }
	
	//메인페이지
	@GetMapping("/auth/user/home")
	public String home() {
		return "layout/user/home";
	}
	
	//회원수정 페이지 이동
    @GetMapping("/auth/user/update")
    public String userUpdate() {
        return "layout/user/update";
    }
	
}
