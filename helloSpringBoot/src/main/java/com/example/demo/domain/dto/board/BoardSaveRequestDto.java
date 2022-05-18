package com.example.demo.domain.dto.board;

import com.example.demo.domain.board.Board;
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
    private String useYn;
    
    private int attachCount;
    private String fileIdxs;
    private String deleteFileIdxs;

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .content(content)
                .count(0)
                .user(user)
                .useYn(useYn)
                .build();
    }

    public void setUser(User user) {
        this.user = user;
    }
}