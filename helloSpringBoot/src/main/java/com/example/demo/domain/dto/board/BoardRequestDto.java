package com.example.demo.domain.dto.board;

import com.example.demo.domain.board.Board;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BoardRequestDto {
	private Long id;
	private String title;
	private String content;
	
	public Board toEntity() {
		return Board.builder()
			.title(title)
			.content(content)
			.build();
	}
}