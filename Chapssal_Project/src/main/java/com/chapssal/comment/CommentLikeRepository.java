package com.chapssal.comment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
    boolean existsByComment_CommentNumAndUser_UserNum(int commentNum, int userNum);
    int countByComment_CommentNum(int commentNum);
    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment.commentNum = :commentNum")
    int countByCommentNum(@Param("commentNum") int commentNum);
    Optional<CommentLike> findByComment_CommentNumAndUser_UserNum(int commentNum, int userNum); // 수정된 부분
}
