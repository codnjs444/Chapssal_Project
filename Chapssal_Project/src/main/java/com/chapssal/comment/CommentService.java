package com.chapssal.comment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final RCommentRepository rCommentRepository;

    @Transactional
    public Comment create(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> findByVideoId(int videoId) {
        return commentRepository.findByVideoVideoNum(videoId);
    }

    public List<Comment> findByVideoNum(int videoNum) {
        return commentRepository.findByVideo_VideoNum(videoNum);
    }

    public int countCommentsByVideoNum(int videoNum) {
        return commentRepository.countByVideoVideoNum(videoNum);
    }

    public List<Comment> getCommentsByVideoNum(int videoNum) {
        return commentRepository.findByVideo_VideoNum(videoNum);
    }
    
    public void setLikeCountsForComments(List<Comment> comments) {
        for (Comment comment : comments) {
            int likeCount = commentLikeRepository.countByCommentNum(comment.getCommentNum());
            comment.setLikeCount(likeCount);
        }
    }
    public Optional<Comment> findById(int commentNum) {
        return commentRepository.findById(commentNum);
    }
    // 특정 댓글에 답글이 있는지 확인하는 메서드 추가
    public boolean hasReplies(int commentNum) {
        return rCommentRepository.countByCommentCommentNum(commentNum) > 0;
    }
}
