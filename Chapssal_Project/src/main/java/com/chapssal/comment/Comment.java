package com.chapssal.comment;

import java.time.LocalDateTime;

import com.chapssal.user.User;
import com.chapssal.video.Video;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentNum")
    private int commentNum;

    @ManyToOne
    @JoinColumn(name = "video", referencedColumnName = "videoNum")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userNum")
    private User user;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private LocalDateTime date;
    
    @Transient
    private boolean isLiked;

    @Transient
    private int likeCount; // 좋아요 수를 저장할 필드

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}