package com.example.demo.domain.board;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.DynamicInsert;

import com.example.demo.domain.reply.Reply;
import com.example.demo.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Entity
@DynamicInsert  //@DynamicInsert, @DynamicUpdate는 각각 insert 또는 update 시 null인 field는 제외 한다.
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    private String content;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int count; //조회수

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;
    
    @OrderBy("id desc")
    @JsonIgnoreProperties({"board"})
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Reply> replyList;
    
    @Column(columnDefinition = "varchar(20) default 'Y'")
    private String useYn;
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<FileEntity> fileEntity = new ArrayList<>();
    
    //글 수정 더티체킹
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    //글 삭제
    public void delete(String useYn) {
    	this.useYn = useYn;
    }
    
}