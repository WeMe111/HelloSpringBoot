package com.example.demo.controller.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.dto.board.BoardDeleteRequestDto;
import com.example.demo.domain.dto.board.BoardSaveRequestDto;
import com.example.demo.domain.dto.board.BoardUpdateRequestDto;
import com.example.demo.security.PrincipalDetail;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BoardApiController {

    private final BoardService boardService;

    //글작성 API
    @PostMapping("/api/v1/board")
    public Long save(@RequestBody BoardSaveRequestDto boardSaveRequestDto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        return boardService.save(boardSaveRequestDto, principalDetail.getUser());
    }
    
    //글삭제 API
//    @DeleteMapping("/api/v1/board/{id}")
//    public Long deleteById(@PathVariable Long id) {
//        boardService.deleteById(id);
//        return id;
//    }
    @PutMapping("/api/v1/board/delete/{id}")
    public Long delete(@PathVariable Long id, @RequestBody BoardDeleteRequestDto boardDeleteRequestDto) {
        return boardService.delete(id, boardDeleteRequestDto);
    }
    
    // 글수정 API
    @PutMapping("/api/v1/board/{id}")
    public Long update(@PathVariable Long id, @RequestBody BoardUpdateRequestDto boardUpdateRequestDto) {
        return boardService.update(id, boardUpdateRequestDto);
    }
    
}