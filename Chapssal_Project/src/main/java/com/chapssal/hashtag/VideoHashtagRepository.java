package com.chapssal.hashtag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chapssal.video.Video;

public interface VideoHashtagRepository extends JpaRepository<VideoHashtag, Integer> {
    boolean existsByVideoAndHashtag(Video video, Hashtag hashtag);
    
    @Query("SELECT vh.video FROM VideoHashtag vh WHERE vh.hashtag.tag = :tag")
    List<Video> findVideosByHashtagTag(@Param("tag") String tag);
}
