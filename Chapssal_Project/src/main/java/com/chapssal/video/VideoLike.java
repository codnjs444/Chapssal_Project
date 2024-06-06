package com.chapssal.video;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "videolike")
public class VideoLike {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vlikeNum;

    @Column(name = "video")
    private Integer video;

    @Column(name = "user")
    private Integer user;

    @Column(name = "likedate")
    private LocalDateTime likeDate;
    
}