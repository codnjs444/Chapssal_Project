package com.chapssal.video;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chapssal.user.User;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Integer> {
	boolean existsByVideoAndUser(Video video, User user);
    VideoLike findOneByVideoAndUser(Video video, User  user);
    
    @Query("SELECT COUNT(vl) FROM VideoLike vl WHERE vl.video = :videoId")
    int countByVideoId(@Param("videoId") Integer videoId);
    
    int countByVideo(Video Video);
    List<VideoLike> findByVideoAndUser(Video video, User user);
    void deleteByVideoAndUser(Video video, User user); // 추가된 부분
    
    @Query("SELECT vl.video, COUNT(vl) as likeCount FROM VideoLike vl " +
	       "WHERE vl.likeDate >= :startDate AND vl.likeDate < :endDate " +
	       "GROUP BY vl.video ORDER BY likeCount DESC")
	List<Object[]> findLikeCountForVideosInWeek(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<VideoLike> findByVideoAndUserAndLikeDateBetween(Video video, User user, LocalDateTime startDate, LocalDateTime endDate);

    List<VideoLike> findByVideo_User_School_SchoolNameAndLikeDateBetween(String schoolName, LocalDateTime startDate, LocalDateTime endDate);
}
