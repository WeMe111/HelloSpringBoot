package com.example.demo.controller.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.User;
import com.example.demo.security.PrincipalDetails;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserApiController {
	
	 private final UserService userService;

	//회원 수정
		@PutMapping("/api/v1/user")
		public Long update(@RequestBody User user,
						   @AuthenticationPrincipal PrincipalDetails principalDetails) {
			userService.Update(user, principalDetails);
			return user.getIdu();
		}
}
