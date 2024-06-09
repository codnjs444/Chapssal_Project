package com.chapssal.video;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chapssal.user.User;

public interface VideoRepository extends JpaRepository<Video, Integer> {

	int countByUser_UserNum(Integer userNum);
    List<Video> findByUserInOrderByVideoNumDesc(List<User> users);
    List<Video> findByUser_UserNum(Integer userNum);
    List<Video> findByUser_UserNumOrderByUploadDateDesc(Integer userNum);
    List<Video> findByUser_School_SchoolNameAndUploadDateBetween(String schoolName, LocalDateTime startDate, LocalDateTime endDate);

    Optional<Video> findFirstByUser_UserNumAndVideoNumLessThanOrderByVideoNumDesc(int userNum, int videoNum);
    Optional<Video> findFirstByUser_UserNumAndVideoNumGreaterThanOrderByVideoNumAsc(int userNum, int videoNum);

    @Query("SELECT v FROM Video v " +
            "LEFT JOIN VideoLike vl ON v = vl.video " +
            "WHERE v.topic = :topic " +
            "GROUP BY v.videoNum ORDER BY COUNT(vl) DESC")
    List<Video> findTopVideosByTopic(Integer topic);

    @Query("SELECT v, COUNT(vl) as likeCount FROM Video v " +
            "LEFT JOIN VideoLike vl ON v = vl.video " +
            "GROUP BY v.videoNum ORDER BY likeCount DESC")
    List<Object[]> findTopVideos();

    @Query("SELECT v FROM Video v WHERE v.user IN :users")
    List<Video> findVideosByUsers(@Param("users") List<User> users);
    
    @Query(value = "SELECT v.videoNum, v.title, v.videoUrl, v.thumbnailUrl, v.user, v.topic, COUNT(vl.vlikeNum) as likeCount " +
            "FROM video v LEFT JOIN videolike vl ON v.videoNum = vl.video " +
            "WHERE vl.likeDate >= NOW() - INTERVAL 1 HOUR " +
            "GROUP BY v.videoNum " +
            "ORDER BY likeCount DESC", nativeQuery = true)
    List<Object[]> findTopVideosByLikesInLastHour();
    
    @Query(value = "SELECT v.videoNum, v.title, v.videoUrl, v.thumbnailUrl, u.userNum, v.topic, COUNT(vl.vlikeNum) as likeCount, " +
            "SUM(CASE WHEN vl.likeDate >= NOW() - INTERVAL 1 HOUR THEN 1 ELSE 0 END) AS recentLikeCount, v.viewCount " +
            "FROM video v LEFT JOIN videolike vl ON v.videoNum = vl.video " +
            "LEFT JOIN user u ON v.user = u.userNum " +
            "GROUP BY v.videoNum, v.title, v.videoUrl, v.thumbnailUrl, u.userNum, v.topic, v.viewCount " +
            "ORDER BY recentLikeCount DESC, likeCount DESC", nativeQuery = true)
    List<Object[]> findAllVideosOrderedByLikes();

    @Query("SELECT v, COUNT(vl) as likeCount FROM Video v " +
            "LEFT JOIN VideoLike vl ON v = vl.video " +
            "AND vl.likeDate >= :startDate AND vl.likeDate < :endDate " +
            "WHERE v.uploadDate >= :startDate AND v.uploadDate < :endDate " +
            "GROUP BY v.videoNum ORDER BY likeCount DESC")
    List<Object[]> findTopVideosForWeek(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT v, COUNT(vl) as likeCount FROM Video v " +
            "LEFT JOIN VideoLike vl ON v = vl.video " +
            "AND vl.likeDate >= :startDate AND vl.likeDate < :endDate " +
            "WHERE v.uploadDate >= :startDate AND v.uploadDate < :endDate AND v.topic = :topic " +
            "GROUP BY v.videoNum ORDER BY likeCount DESC")
    List<Object[]> findTopVideosForWeekAndTopic(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("topic") int topic);

    List<Video> findByTitleContaining(String title);


    List<Video> findByTopicOrderByVideoNumAsc(int topic);
    
    @Query("SELECT v FROM Video v ORDER BY v.uploadDate DESC")
    List<Video> findAllVideosOrderByUploadDateDesc();
    
    @Query(value = "SELECT v.videoNum, v.title, v.videoUrl, v.thumbnailUrl, u.userNum, v.topic, COUNT(vl.vlikeNum) as likeCount, " +
            "SUM(CASE WHEN vl.likeDate >= NOW() - INTERVAL 1 HOUR THEN 1 ELSE 0 END) AS recentLikeCount, v.viewCount " +
            "FROM video v LEFT JOIN videolike vl ON v.videoNum = vl.video " +
            "LEFT JOIN user u ON v.user = u.userNum " +
            "WHERE v.topic = :topic " +
            "GROUP BY v.videoNum, v.title, v.videoUrl, v.thumbnailUrl, u.userNum, v.topic, v.viewCount " +
            "ORDER BY recentLikeCount DESC, likeCount DESC", nativeQuery = true)
    List<Object[]> findAllVideosOrderedByLikesAndTopic(@Param("topic") int topic);
    
//    Page<Video> findAll(Pageable pageable);
}
