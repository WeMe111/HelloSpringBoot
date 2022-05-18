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
public class BoardDeleteRequestDto {
	private Long id;
	private String useYn;
	
	public Board toEntity() {
		return Board.builder()
			.useYn(useYn)
			.build();
	}
}
