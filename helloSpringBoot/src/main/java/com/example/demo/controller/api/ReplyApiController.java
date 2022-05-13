package com.example.demo.controller.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Reply;
import com.example.demo.security.PrincipalDetails;
import com.example.demo.service.ReplyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ReplyApiController {

	private final ReplyService replyService;
	
	@PostMapping("/board/{idb}/reply")
	public void save(@PathVariable Long idb,
					 @RequestBody Reply reply,
					 @AuthenticationPrincipal PrincipalDetails principalDetail) {
		
		replyService.replySave(idb, reply, principalDetail.getUser());
	}
	
}
