package com.example.demo.controller.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.reply.Reply;
import com.example.demo.security.PrincipalDetail;
import com.example.demo.service.ReplyService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ReplyApiController {
	
	private final ReplyService replyService;

	//댓글저장
    @PostMapping("/api/v1/board/{boardId}/reply")
    public void save(@PathVariable Long boardId,
                     @RequestBody Reply reply,
                     @AuthenticationPrincipal PrincipalDetail principalDetail) {
        replyService.replySave(boardId, reply, principalDetail.getUser());
    }

    //댓글삭제
    @DeleteMapping("/api/v1/board/{boardId}/reply/{replyId}")
    public void delete(@PathVariable Long replyId) {
        replyService.replyDelete(replyId);
    }
    
}
