package com.chapssal.video;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Integer> {
    List<VideoLike> findByVideo_User_School_SchoolNameAndLikeDateBetween(String schoolName, LocalDateTime startDate, LocalDateTime endDate);
}
