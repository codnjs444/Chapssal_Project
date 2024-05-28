package com.chapssal.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	List<Comment> findByVideoVideoNum(int videoNum);
}
