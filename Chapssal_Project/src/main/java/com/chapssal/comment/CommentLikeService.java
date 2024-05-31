package com.chapssal.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chapssal.user.User;
import com.chapssal.user.UserRepository;

import java.time.LocalDateTime;

@Service
public class CommentLikeService {

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    public void likeComment(int commentNum, int currentUserNum, LocalDateTime likeDate) {
        Comment comment = commentRepository.findById(commentNum).orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(currentUserNum).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        CommentLike existingLike = commentLikeRepository.findByComment_CommentNumAndUser_UserNum(commentNum, currentUserNum);
        if (existingLike != null) {
            // 이미 좋아요가 눌려져 있는 상태라면 좋아요를 취소합니다.
            commentLikeRepository.delete(existingLike);
        } else {
            // 좋아요를 누릅니다.
            CommentLike commentLike = new CommentLike();
            commentLike.setComment(comment);
            commentLike.setUser(user);
            commentLike.setLikeDate(likeDate);
            commentLikeRepository.save(commentLike);
        }
    }

    public boolean isCommentLikedByUser(int commentNum, int userNum) {
        return commentLikeRepository.existsByComment_CommentNumAndUser_UserNum(commentNum, userNum);
    }
}
