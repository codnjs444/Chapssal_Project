package com.chapssal.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RCommentDTO {
    private int rcommentNum;
    private int commentNum;
    private int userNum;
    private String userName;
    private String text;
    private LocalDateTime date;
    private String profilePictureUrl;
    
    public RCommentDTO(RComment rComment) {
        this.rcommentNum = rComment.getRcommentNum();
        this.commentNum = rComment.getComment().getCommentNum();
        this.userNum = rComment.getUser().getUserNum();
        this.userName = rComment.getUser().getUserName();
        this.text = rComment.getText();
        this.profilePictureUrl = rComment.getUser().getProfilePictureUrl();
        this.date = rComment.getDate();
    }
}
