package com.example.demo.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BoardFile {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    private Long boardIdx;

    private String useYn;

    public BoardFile(Long boardIdx, Long fileId, String useYn) {
        this.boardIdx = boardIdx;
        this.fileId = fileId;
        this.useYn = useYn;
    }
    
}
