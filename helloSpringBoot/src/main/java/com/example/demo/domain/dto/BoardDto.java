package com.example.demo.domain.dto;

import java.security.Timestamp;
import java.util.List;

import com.example.demo.domain.Board;
import com.example.demo.domain.Reply;
import com.example.demo.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

	 	private Long idb;
	    private String title;
	    private String content;
	    private String Writer;
	    private int count;
	    private User user;
	    private Timestamp createDate;
	    private Timestamp updateDate;
	    private List<Reply> reply;
	    private String useYn;

	    public Board toEntity(){
	        Board boardEntity = Board.builder()
	                .idb(idb)
	                .title(title)
	                .content(content)
	                .user(user)
	                .count(count)
	                .reply(reply)
	                .writer(Writer)
	                .useYn(useYn)
	                .build();
	        return boardEntity;
	    }
}
