package com.ccp5.repository;

import com.ccp5.dto.CommentDTO;
import com.ccp5.dto.BoardDTO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentDTO, Long> {

	List<CommentDTO> findByBoard(BoardDTO board);
}
