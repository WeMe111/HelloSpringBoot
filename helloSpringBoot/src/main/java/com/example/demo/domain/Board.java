package com.example.demo.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
	
	@Id
	@GeneratedValue
	private Long idb;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private User user;
	
//	@OneToMany(mappedBy = "board")
//	private List<Reply> comment = new ArrayList<>();
	
	@Column(nullable = false)
	private String title;
	
	@Lob
	private String content;
	
	@Column(nullable = false)
	private String writer;
	
	@Column(columnDefinition = "integer default 0", nullable = false)
	private int count;
	
	@CreationTimestamp
	@Column(updatable = false)
	private Timestamp createDate;
	
	@UpdateTimestamp
	private Timestamp updateDate;
	
	@Builder
	public Board(Long idb, String title, String content, String writer, User user) {
		this.idb = idb;
		this.writer = writer;
		this.title = title;
		this.content = content;
		this.user = user;
	}

}
