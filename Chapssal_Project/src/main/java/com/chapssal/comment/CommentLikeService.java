package com.chapssal.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chapssal.user.User;
import com.chapssal.user.UserRepository;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentLikeService {

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void likeComment(int commentNum, int currentUserNum, LocalDateTime likeDate) {
        Comment comment = commentRepository.findById(commentNum).orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        User user = userRepository.findById(currentUserNum).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLike.setLikeDate(likeDate);

        commentLikeRepository.save(commentLike);
    }
    @Transactional
    public void unlikeComment(int commentNum, int currentUserNum) {
        Optional<CommentLike> commentLikeOpt = commentLikeRepository.findByComment_CommentNumAndUser_UserNum(commentNum, currentUserNum);
        if (!commentLikeOpt.isPresent()) {
            throw new IllegalArgumentException("좋아요를 찾을 수 없습니다.");
        }
        CommentLike commentLike = commentLikeOpt.get();
        commentLikeRepository.delete(commentLike);
    }

    @Transactional(readOnly = true)
    public boolean isCommentLikedByUser(int commentNum, int userNum) {
        return commentLikeRepository.existsByComment_CommentNumAndUser_UserNum(commentNum, userNum);
    }
    
    @Transactional(readOnly = true)
    public int getCommentLikeCount(int commentNum) {
        return commentLikeRepository.countByComment_CommentNum(commentNum);
    }
    
    @Transactional(readOnly = true)
    public int countByCommentNum(int commentNum) {
        return commentLikeRepository.countByComment_CommentNum(commentNum);
    }
}
