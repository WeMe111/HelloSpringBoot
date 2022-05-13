package com.example.demo.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Reply {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idr;
	
	private String content;
	
	@ManyToOne
	@JoinColumn(name = "idb")
	private Board board;
	
	@ManyToOne
	@JoinColumn(name = "idu")
	private User user;
	
	public void save(Board board, User user) {
		this.board = board;
		this.user = user;
	}

}
