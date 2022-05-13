package com.example.demo.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Board extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idb;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idu")
	private User user;
	
	@OrderBy("idb desc")  //댓글 작성시 최근 순으로 볼 수 있도록 설정
	@JsonIgnoreProperties({"board"})
	@OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Reply> reply;
	
	@Column(nullable = false)
	private String title;
	
	@Lob
	@Column(nullable = false)
	private String content;
	
	private String writer;
	
	@Column(columnDefinition = "integer default 0", nullable = false)
	private int count;
	
	private String useYn;
	
	//첨부파일 개수
	private Long attachCount;
	
	@Transient
	private String fileIdxs;
	
	@Transient
	private String deleteFileIdxs;
	
//	@CreationTimestamp
//	@Column(updatable = false)
//	private Timestamp createDate;
//	
//	@UpdateTimestamp
//	private Timestamp updateDate;
	
}
