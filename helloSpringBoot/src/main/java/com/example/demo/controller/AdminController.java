package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.domain.user.User;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AdminController {
	
	private final UserService userService;
	
	@GetMapping("/admin/userList")
	public String userList(Model model) {
		List<User> user = userService.findUser();
		model.addAttribute("user", user);
		return "user/admin/userList";
	}

}
