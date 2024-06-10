package com.chapssal.comment;

import com.chapssal.user.User;
import com.chapssal.user.UserService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/rcomment")
public class RCommentController {
    private final RCommentService rCommentService;
    private final CommentService commentService;
    private final UserService userService;
    private final RCommentRepository rCommentRepository;
    
    public RCommentController(RCommentService rCommentService, CommentService commentService, UserService userService, RCommentRepository rCommentRepository) {
        this.rCommentService = rCommentService;
        this.commentService = commentService;
        this.userService = userService;
        this.rCommentRepository = rCommentRepository;
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
    
    @Transactional
    public RComment create(RComment rComment) {
        return rCommentRepository.save(rComment);
    }

    @GetMapping("/list")
    public List<RCommentDTO> getRComments(@RequestParam("commentNum") int commentNum) {
        List<RComment> rComments = rCommentService.findByCommentNum(commentNum);
        return rComments.stream()
                .map(RCommentDTO::new)
                .collect(Collectors.toList());
    }
    
    public List<RComment> findByCommentNum(int commentNum) {
        return rCommentRepository.findByCommentCommentNum(commentNum);
    }
}