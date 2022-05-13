package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.BoardFile;
import com.example.demo.domain.FileEntity;

@Repository
public interface BoardFileRepository extends CrudRepository<BoardFile, Long>{

	@Query(value =
            "SELECT \n"+
                    "T1.FILE_ID\n"+
                    ",T1.BOARD_IDX\n"+
                    ",T1.USE_YN\n"+
                    ",T2.ORIG_NM AS ORIG_NM\n"+
            "FROM \n"+
            "board_file T1\n"+
            ",file T2\n"+
            "WHERE\n"+
            "T1.FILE_ID = T2.FILE_IDX\n"+
            "AND T1.USE_YN = 'Y'\n"+
            "AND T1.BOARD_IDX = :boardIdx\n"
            , nativeQuery = true
    )
    List<BoardFile> findByBoardIdx(Long boardIdx);
	
}
