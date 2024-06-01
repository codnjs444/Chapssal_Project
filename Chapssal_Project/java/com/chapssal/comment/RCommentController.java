package com.chapssal.comment;

import com.chapssal.user.User;
import com.chapssal.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/rcomment")
public class RCommentController {
    private final RCommentService rCommentService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public RCommentController(RCommentService rCommentService, CommentService commentService, UserService userService) {
        this.rCommentService = rCommentService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/add", consumes = "application/json")
    public String addRComment(@RequestBody RComment rComment, Principal principal) {
        User user = userService.getUser(principal.getName());
        Comment comment = commentService.findById(rComment.getComment().getCommentNum())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        rComment.setUser(user);
        rComment.setComment(comment);
        rComment.setDate(LocalDateTime.now());

        rCommentService.create(rComment);

        return "success";
    }
}
