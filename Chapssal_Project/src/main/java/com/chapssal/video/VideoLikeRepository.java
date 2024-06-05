package com.chapssal.video;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Integer> {
    boolean existsByVideoAndUser(Integer video, Integer user);
    VideoLike findByVideoAndUser(Integer video, Integer user);

    @Query("SELECT COUNT(vl) FROM VideoLike vl WHERE vl.video = :videoId")
    int countByVideoId(@Param("videoId") Integer videoId);

    int countByVideo(int videoId);
    List<VideoLike> findByVideoAndUser(int videoId, int userId);
    void deleteByVideoAndUser(int videoId, int userId); // 추가된 부분

    @Query("SELECT vl.video, COUNT(vl) as likeCount FROM VideoLike vl " +
            "WHERE vl.likeDate >= :startDate AND vl.likeDate < :endDate " +
            "GROUP BY vl.video ORDER BY likeCount DESC")
    List<Object[]> findLikeCountForVideosInWeek(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}