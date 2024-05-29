package com.chapssal.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chapssal.video.Video;

public interface VideoHashtagRepository extends JpaRepository<VideoHashtag, Integer> {
    boolean existsByVideoAndHashtag(Video video, Hashtag hashtag);
}
