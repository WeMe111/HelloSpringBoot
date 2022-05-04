package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.domain.dto.BoardDto;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

	public final BoardService boardService;
	
	@GetMapping("/board/list")
    public String list(Model model) {
        model.addAttribute("board", boardService.list());
        return "board/list";
    }

    @GetMapping("/board/register")
    public String registerGet() {
        return "board/register";
    }

    @PostMapping("/board/register")
    public String registerPost(BoardDto boardDto) {
        boardService.register(boardDto);
        return "redirect:/";
    }

}
