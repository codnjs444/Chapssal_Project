package com.chapssal.comment;

import com.chapssal.user.User;
import com.chapssal.user.UserService;
import com.chapssal.video.Video;
import com.chapssal.video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final VideoService videoService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, VideoService videoService, UserService userService) {
        this.commentService = commentService;
        this.videoService = videoService;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/add", consumes = "application/json")
    public String addComment(@RequestBody Comment comment, Principal principal) {
        User user = userService.getUser(principal.getName());
        Video video = videoService.findById(comment.getVideo().getVideoNum()).orElseThrow(() -> new RuntimeException("Video not found"));

        comment.setUser(user);
        comment.setVideo(video);
        comment.setDate(LocalDateTime.now());

        commentService.create(comment);

        return "success";
    }

    @GetMapping("/video/{videoNum}")
    public List<Comment> getCommentsByVideo(@PathVariable("videoNum") int videoNum) {
        return commentService.findByVideoId(videoNum);
    }
}
