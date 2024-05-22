package com.chapssal.video;

import com.chapssal.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "video")
@Getter
@Setter
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "videoNum")
    private int videoNum;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userNum")
    private User user;

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
