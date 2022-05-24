package com.example.demo.domain.dto.board;

import java.util.List;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.board.FileEntity;
import com.example.demo.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardSaveRequestDto {

	private Long id;
    private String title;
    private String content;
    private int count;
    private User user;
    
    private Board board;

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .content(content)
                .count(0)
                .user(user)
                .build();
    }

    public void setUser(User user) {
        this.user = user;
    }    
  
}