package com.chapssal.comment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

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
}
