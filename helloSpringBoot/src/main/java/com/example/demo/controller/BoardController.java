package com.example.demo.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.FileEntity;
import com.example.demo.domain.dto.board.BoardSaveRequestDto;
import com.example.demo.repository.FileRepository;
import com.example.demo.security.PrincipalDetail;
import com.example.demo.service.BoardService;
import com.example.demo.service.FileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardService boardService;
	private final FileRepository fileRepository;
	private final FileService fileService;
	
	
	// 게시판 페이지 이동
//	@GetMapping("/auth/board/list")
//	public String index(Model model,
//			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
//			@RequestParam(required = false, defaultValue = "") String search) {
//		Page<Board> boards = boardService.findByTitleContainingOrContentContaining(search, search, pageable);
//		int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
//		int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
//		model.addAttribute("startPage", startPage);
//		model.addAttribute("endPage", endPage);
//		model.addAttribute("boards", boards);
//		return "layout/board/list";
//	}
	@GetMapping("/auth/board/list")
	public String list(Model model,
			@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam(required = false, defaultValue = "") String keyword,
			@RequestParam(required = false, defaultValue = "") String select) {
		Page<Board> boards = boardService.selectList(pageable, select, keyword);
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
	
	// 글작성
	@PostMapping("/auth/board/register")
	public String registerPost(BoardSaveRequestDto boardSaveRequestDto,
							   @AuthenticationPrincipal PrincipalDetail principalDetail,
							   @RequestParam("file") MultipartFile file,
							   @RequestParam("files") List<MultipartFile> files) throws IOException {
		
		fileService.saveFile(file);

        for (MultipartFile multipartFile : files) {
            fileService.saveFile(multipartFile);
        }
        
		boardService.register(boardSaveRequestDto, principalDetail.getUser());
		return "redirect:/auth/board/list";
	}

	// 글상세 페이지 이동
	@GetMapping("/auth/board/{id}")
	public String detail(@PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
		// 쿠키를 이용한 조회수 중복 방지
		Cookie oldCookie = null;
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if (cookie.getName().equals("postView")) {
	                oldCookie = cookie;
	            }
	        }
	    }
	    if (oldCookie != null) {
	        if (!oldCookie.getValue().contains("[" + id.toString() + "]")) {
	        	boardService.updateCount(id);
	            oldCookie.setValue(oldCookie.getValue() + "_[" + id + "]");
	            oldCookie.setPath("/");
	            oldCookie.setMaxAge(60 * 60 * 24);
	            response.addCookie(oldCookie);
	        }
	    } else {
	    	boardService.updateCount(id);
	        Cookie newCookie = new Cookie("postView","[" + id + "]");
	        newCookie.setPath("/");
	        newCookie.setMaxAge(60 * 60 * 24);
	        response.addCookie(newCookie);
	    }
	    // 저장된 파일을 불러오는 로직(수정필요)
	    List<FileEntity> files = fileRepository.findAll();
	    model.addAttribute("all",files);
	    model.addAttribute("board", boardService.detail(id));
	    
		return "layout/board/detail";
	}

	// 글수정 페이지 이동
	@GetMapping("/board/{id}/update")
	public String update(@PathVariable Long id, Model model) {
		model.addAttribute("board", boardService.detail(id));
		return "layout/board/update";
	}
	
	//===
	//  이미지 출력
   @GetMapping("/images/{fileId}")
   @ResponseBody
   public Resource downloadImage(@PathVariable("fileId") Long id, Model model) throws IOException{

       FileEntity file = fileRepository.findById(id).orElse(null);
       return new UrlResource("file:" + file.getSavedPath());
   }

   // 첨부 파일 다운로드
   @GetMapping("/attach/{id}")
   public ResponseEntity<Resource> downloadAttach(@PathVariable Long id) throws MalformedURLException {

       FileEntity file = fileRepository.findById(id).orElse(null);

       UrlResource resource = new UrlResource("file:" + file.getSavedPath());

       String encodedFileName = UriUtils.encode(file.getOrgNm(), StandardCharsets.UTF_8);

       // 파일 다운로드 대화상자가 뜨도록 하는 헤더를 설정해주는 것
       // Content-Disposition 헤더에 attachment; filename="업로드 파일명" 값을 준다.
       String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

       return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,contentDisposition).body(resource);
   }
}
