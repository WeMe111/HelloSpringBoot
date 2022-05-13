package com.example.demo.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.Board;
import com.example.demo.domain.dto.BoardDto;
import com.example.demo.repository.BoardRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.ReplyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

	private final BoardService boardService;
	private final ReplyService replyService;
	private final BoardRepository boardRepository;
	
	//게시판 목록
	@GetMapping("/board/list")
    public String list(Model model, @PageableDefault(size = 5, sort = "idb", direction = Sort.Direction.DESC) Pageable pageable
    							  , @RequestParam(required = false, defaultValue = "") String search) {
        Page<Board> boards = boardService.search(search, search, pageable);
        int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("boards", boards);
        model.addAttribute("boardDto", boardService.search(search, search, pageable));
        return "board/list";
    }
	//게시판 등록 페이지
    @GetMapping("/board/register")
    public String registerGet() {
        return "board/register";
    }
    //게시판 글 등록
//    @PostMapping("/board/register")
//    public String registerPost(BoardDto boardDto) {
//        boardService.savePost(boardDto);
//        return "redirect:/board/list";
//    }
    @ResponseBody
    @PostMapping("/board/register")
    public Long writeSubmit(@RequestBody Board board){
    	
        log.info("params={}", board);

        boardRepository.save(board);
        
        //board 게시판 테이블 insert
        Long idb = boardService.savePost(board);
        board.setIdb(idb);
        
        //파일 테이블 insert
        boardService.insertBoardFile(board);
        
        return idb;
    };
    
    //상세페이지
    @GetMapping("/board/detail/{idb}")
    public String detail(@PathVariable("idb") Long idb, Model model) {
    	BoardDto boardDto = boardService.getPost(idb);
    	boardService.updateCount(idb);
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
//    @PutMapping("/board/update/{idb}")
//    public String update(BoardDto boardDto) {
//    	boardService.savePost(boardDto);
//    	return "redirect:/board/list";
//    }
    
    //글 삭제
//    @GetMapping("/board/delete/{idb}")
//    public String delete(@PathVariable Long idb) {
//    	boardService.deletePost(idb);
//    	return "redirect:/board/list";
//    }
    
    @ResponseBody
    @PostMapping("/board/delete/{idb}")
    public List<String> deleteSubmit(@RequestBody List<String> boardIdbArray){
        log.info("boardIdxArray={}", boardIdbArray);
        boardService.deleteBoard(boardIdbArray);
        return boardIdbArray;
    }
    
}
