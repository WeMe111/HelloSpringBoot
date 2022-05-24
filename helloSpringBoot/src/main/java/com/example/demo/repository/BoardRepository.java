package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.board.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
	
	//조회수 증가 쿼리
	@Modifying
    @Query("update Board p set p.count = p.count + 1 where p.id = :id")
    int updateCount(@Param("id") Long id);
    
	Page<Board> findByUseYn(String useYn, Pageable pageable);

	Page<Board> findByTitleContainingAndUseYnIgnoreCase(String keyword, String useYn, Pageable pageable);

	Page<Board> findByContentContainingAndUseYnIgnoreCase(String keyword, String useYn, Pageable pageable);
	
}

