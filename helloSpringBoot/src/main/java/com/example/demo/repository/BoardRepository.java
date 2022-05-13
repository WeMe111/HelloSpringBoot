package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

	@Modifying
	@Query("update Board p set p.count = p.count + 1 where p.idb = ?1")
	int updateCount(Long idb);
	
	//검색 조건 Like문 실행
	Page<Board> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
	
//	public boolean deleteBoard(Long idb);
	
	
	//useYn 조건 find
    Page<Board> findAllByUseYn(Pageable pageable, String useYn);

    //useYn boardTitle 조건
    Page<Board> findAllBytitleContainingIgnoreCaseAndUseYn(Pageable pageable, String title, String useYn);
}
