package com.chapssal.comment;

import java.time.LocalDateTime;
import java.util.List;

import com.chapssal.user.User;
import com.chapssal.video.Video;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    
    ///////////////////////
    @OneToMany(mappedBy = "comment")
    private List<RComment> replies; // 답글 목록

    @Transient
    private boolean hasReplies; // 답글 여부

    // 기존 필드와 메서드들

    // Getter와 Setter 추가
    public boolean isHasReplies() {
        return hasReplies;
    }
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RComment> rComments;
    
    public void setHasReplies(boolean hasReplies) {
        this.hasReplies = hasReplies;
    }
    
}