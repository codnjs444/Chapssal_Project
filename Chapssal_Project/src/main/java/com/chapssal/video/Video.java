package com.chapssal.video;

import com.chapssal.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.chapssal.hashtag.Hashtag;
import java.time.LocalDateTime;
import java.util.List;

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

    @Transient // This field is not persisted in the database
    private Long likeCount;
    
    @ManyToMany
    @JoinTable(
            name = "VideoHashtag",
            joinColumns = @JoinColumn(name = "video"),
            inverseJoinColumns = @JoinColumn(name = "hashtag")
    )
    private List<Hashtag> hashtags; // List of hashtags
}