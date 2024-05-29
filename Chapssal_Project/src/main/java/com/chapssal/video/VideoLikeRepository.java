package com.chapssal.video;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Integer> {
	boolean existsByVideoAndUser(Integer video, Integer user);
    VideoLike findByVideoAndUser(Integer video, Integer user);
    
    @Query("SELECT COUNT(vl) FROM VideoLike vl WHERE vl.video = :videoId")
    int countByVideoId(@Param("videoId") Integer videoId);
}
