package com.chapssal.comment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
    boolean existsByComment_CommentNumAndUser_UserNum(int commentNum, int userNum);
    CommentLike findByComment_CommentNumAndUser_UserNum(int commentNum, int userNum);
}
