package com.chapssal.hashtag;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hashtag")
@Getter
@Setter
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtagNum")
    private int hashtagNum;

    @Column(name = "tag", unique = true, nullable = false)
    private String tag;

    @Column(name = "hashtagCount", nullable = false)
    private Integer hashtagCount;
}
