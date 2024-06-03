package com.chapssal.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RCommentRepository extends JpaRepository<RComment, Integer> {
    List<RComment> findByCommentCommentNum(int commentNum);
    int countByCommentCommentNum(int commentNum); // 이 메서드 추가
}

