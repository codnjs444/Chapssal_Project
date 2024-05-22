package com.chapssal.video;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "video")
@Getter
@Setter
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "videoNum")
    private int videoNum;

    @Column(name = "user")
    private Integer user;

    @Column(name = "title")
    private String title;

    @Column(name = "videoUrl")
    private String videoUrl;

    @Column(name = "thumbnailUrl")
    private String thumbnailUrl;

    @Column(name = "topic")
    private Integer topic;

    @Column(name = "uploadDate")
    private LocalDateTime uploadDate;

    @Column(name = "viewCount")
    private Integer viewCount;
}