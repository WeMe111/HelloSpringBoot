package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.domain.board.Board;
import com.example.demo.service.BoardService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardService boardService;

	// 게시판 페이지 이동
	@GetMapping("/auth/board/list")
	public String index(Model model,
			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(required = false, defaultValue = "") String search) {
		Page<Board> boards = boardService.findByTitleContainingOrContentContaining(search, search, pageable);
		int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
		int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("boards", boards);
		return "layout/board/list";
	}

	// 글작성 페이지 이동
	@GetMapping("/auth/board/register")
	public String save() {
		return "layout/board/register";
	}

	// 글상세 페이지 이동
	@GetMapping("/auth/board/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("board", boardService.detail(id));
		boardService.updateCount(id);
		return "layout/board/detail";
	}

	// 글수정 페이지 이동
	@GetMapping("/board/{id}/update")
	public String update(@PathVariable Long id, Model model) {
		model.addAttribute("board", boardService.detail(id));
		return "layout/board/update";
	}

}
