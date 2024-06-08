package com.chapssal.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@RestController
@RequestMapping("/comment")
public class CommentLikeController {

    @Autowired
    private CommentLikeService commentLikeService;

    @PostMapping("/like")
    public ResponseEntity<?> likeComment(@RequestBody CommentLikeRequest request) {
        try {
            LocalDateTime likeDate = LocalDateTime.parse(request.getLikeDate(), DateTimeFormatter.ISO_DATE_TIME);
            commentLikeService.likeComment(request.getCommentNum(), request.getCurrentUserNum(), likeDate);
            int likeCount = commentLikeService.countByCommentNum(request.getCommentNum());
            return ResponseEntity.ok().body(Collections.singletonMap("likeCount", likeCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<?> unlikeComment(@RequestBody CommentLikeRequest request) {
        try {
            commentLikeService.unlikeComment(request.getCommentNum(), request.getCurrentUserNum());
            int likeCount = commentLikeService.countByCommentNum(request.getCommentNum());
            return ResponseEntity.ok().body(Collections.singletonMap("likeCount", likeCount));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
