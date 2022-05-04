package com.example.demo.domain.dto;

import java.security.Timestamp;

import com.example.demo.domain.Board;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardDto {

	 	private Long idb;
	    private String writer;
	    private String title;
	    private String content;
	    private Timestamp createdDate;
	    private Timestamp updateDate;

	    public Board toEntity(){
	        Board boardEntity = Board.builder()
	                .idb(idb)
	                .writer(writer)
	                .title(title)
	                .content(content)
	                .build();
	        return boardEntity;
	    }

	    @Builder
	    public BoardDto(Long idb, String title, String content, String writer, Timestamp createdDate, Timestamp updateDate) {
	        this.idb = idb;
	        this.writer = writer;
	        this.title = title;
	        this.content = content;
	        this.createdDate = createdDate;
	        this.updateDate = updateDate;
	    }
}
