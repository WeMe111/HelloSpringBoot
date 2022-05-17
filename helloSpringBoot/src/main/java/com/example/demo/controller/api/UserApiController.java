package com.example.demo.controller.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.dto.user.UserSaveRequestDto;
import com.example.demo.domain.user.User;
import com.example.demo.security.PrincipalDetail;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    //회원가입 API
    @PostMapping("/auth/api/v1/user")
    public Long save(@RequestBody UserSaveRequestDto userSaveRequestDto) {
        return userService.save(userSaveRequestDto.toEntity());
    }
    
    //회원 수정 API
    @PutMapping("/api/v1/user")
    public Long update(@RequestBody User user, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        userService.update(user, principalDetail);
        return user.getId();
    }
    
}