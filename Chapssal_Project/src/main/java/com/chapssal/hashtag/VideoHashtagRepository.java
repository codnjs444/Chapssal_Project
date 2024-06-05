package com.chapssal.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chapssal.video.Video;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoHashtagRepository extends JpaRepository<VideoHashtag, Integer> {
    boolean existsByVideoAndHashtag(Video video, Hashtag hashtag);

    @Query("SELECT vh.video FROM VideoHashtag vh WHERE vh.hashtag.tag = :tag")
    List<Video> findVideosByHashtagTag(@Param("tag") String tag);
}
