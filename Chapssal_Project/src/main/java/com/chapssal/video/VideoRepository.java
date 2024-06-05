package com.chapssal.video;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chapssal.user.User;

public interface VideoRepository extends JpaRepository<Video, Integer> {

	int countByUser_UserNum(Integer userNum);
    List<Video> findByUserInOrderByVideoNumDesc(List<User> users);
    List<Video> findByUser_UserNum(Integer userNum);
    List<Video> findByUser_UserNumOrderByUploadDateDesc(Integer userNum);

    Optional<Video> findFirstByUser_UserNumAndVideoNumLessThanOrderByVideoNumDesc(int userNum, int videoNum);
    Optional<Video> findFirstByUser_UserNumAndVideoNumGreaterThanOrderByVideoNumAsc(int userNum, int videoNum);

    @Query("SELECT v FROM Video v LEFT JOIN VideoLike vl ON v.videoNum = vl.video WHERE v.topic = :topic GROUP BY v.videoNum ORDER BY COUNT(vl) DESC")
    List<Video> findTopVideosByTopic(@Param("topic") Integer topic);

    @Query("SELECT v, COUNT(vl) as likeCount FROM Video v LEFT JOIN VideoLike vl ON v.videoNum = vl.video GROUP BY v.videoNum ORDER BY likeCount DESC")
    List<Object[]> findTopVideos();

    @Query("SELECT v FROM Video v WHERE v.user IN :users")
    List<Video> findVideosByUsers(@Param("users") List<User> users);
    
    @Query(value = "SELECT v.videoNum, v.title, v.videoUrl, v.thumbnailUrl, v.user, v.topic, COUNT(vl.vlikeNum) as likeCount " +
            "FROM video v LEFT JOIN videolike vl ON v.videoNum = vl.video " +
            "WHERE vl.likeDate >= NOW() - INTERVAL 1 HOUR " +
            "GROUP BY v.videoNum " +
            "ORDER BY likeCount DESC", nativeQuery = true)
    List<Object[]> findTopVideosByLikesInLastHour();
    
    @Query(value = "SELECT v.videoNum, v.title, v.videoUrl, v.thumbnailUrl, v.user, v.topic, COUNT(vl.vlikeNum) as likeCount, " +
            "SUM(CASE WHEN vl.likeDate >= NOW() - INTERVAL 1 HOUR THEN 1 ELSE 0 END) as recentLikeCount " +
            "FROM video v LEFT JOIN videolike vl ON v.videoNum = vl.video " +
            "GROUP BY v.videoNum " +
            "ORDER BY recentLikeCount DESC, likeCount DESC", nativeQuery = true)
    List<Object[]> findAllVideosOrderedByLikes();

    @Query("SELECT v, COUNT(vl) as likeCount FROM Video v LEFT JOIN VideoLike vl ON v.videoNum = vl.video " +
           "WHERE v.uploadDate >= :startDate AND v.uploadDate < :endDate " +
           "GROUP BY v.videoNum ORDER BY likeCount DESC")
    List<Object[]> findTopVideosForWeek(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT v FROM Video v ORDER BY v.uploadDate DESC")
    List<Video> findAllVideosOrderByUploadDateDesc();
}
