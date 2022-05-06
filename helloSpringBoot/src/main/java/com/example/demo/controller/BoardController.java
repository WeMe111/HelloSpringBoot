package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.demo.domain.dto.BoardDto;
import com.example.demo.repository.BoardRepository;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

	public final BoardService boardService;
	
	//게시판 목록
	@GetMapping("/board/list")
    public String list(Model model) {
        model.addAttribute("board", boardService.list());
        return "board/list";
    }
	//게시판 등록 페이지
    @GetMapping("/board/register")
    public String registerGet() {
        return "board/register";
    }
    //게시판 글 등록
    @PostMapping("/board/register")
    public String registerPost(BoardDto boardDto) {
        boardService.savePost(boardDto);
        return "redirect:/user/home";
    }
    //상세페이지
    @GetMapping("/board/detail/{idb}")
    public String detail(@PathVariable("idb") Long idb, Model model) {
    	BoardDto boardDto = boardService.getPost(idb);
    	model.addAttribute("boardDto", boardDto);
    	return "board/detail";
    }
    //수정페이지
    @GetMapping("/board/update/{idb}")
    public String edit(@PathVariable("idb") Long idb, Model model) {
    	BoardDto boardDto = boardService.getPost(idb);
    	model.addAttribute("boardDto", boardDto);
    	return "board/update";
    }
    //글 수정
    @PutMapping("/board/update/{idb}")
    public String update(BoardDto boardDto) {
    	boardService.savePost(boardDto);
    	return "redirect:/board/list";
    }
    //글 삭제
    @GetMapping("/board/delete/{idb}")
    public String delete(@PathVariable Long idb) {
    	boardService.deletePost(idb);
    	return "redirect:/board/list";
    }
    
}
