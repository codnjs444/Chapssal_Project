package com.chapssal.award;

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
import lombok.Getter;
import lombok.Setter;

import com.chapssal.topic.Topic;

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

    @ManyToOne
    @JoinColumn(name = "topic", referencedColumnName = "topicNum")
    private Topic topic;

    @Column(nullable = false)
    private String awardName;

    @Column(nullable = false)
    private String awardType;

    @Column(nullable = false)
    private String awardDate;

}
