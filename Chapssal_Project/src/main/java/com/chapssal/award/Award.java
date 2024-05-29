package com.chapssal.award;

import com.chapssal.user.User;
import com.chapssal.video.Video;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Award")
@Getter
@Setter
public class Award {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int awardNum;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userNum")
    private User user;

    @ManyToOne
    @JoinColumn(name = "video", referencedColumnName = "videoNum")
    private Video video;

    @Column(nullable = false)
    private String awardName;

    @Column(nullable = false)
    private int awardType; 

    @Column(nullable = false)
    private LocalDateTime awardDate;  
}