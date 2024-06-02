package com.chapssal.video;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chapssal.user.User;

public interface VideoRepository extends JpaRepository<Video, Integer> {
	int countByUser_UserNum(Integer userNum);
    List<Video> findByUserInOrderByVideoNumAsc(List<User> users);
    List<Video> findByUser_UserNum(Integer userNum);
    List<Video> findByUser_UserNumOrderByUploadDateDesc(Integer userNum);

    Optional<Video> findFirstByUser_UserNumAndVideoNumLessThanOrderByVideoNumDesc(int userNum, int videoNum);
    Optional<Video> findFirstByUser_UserNumAndVideoNumGreaterThanOrderByVideoNumAsc(int userNum, int videoNum);

    @Query("SELECT v FROM Video v LEFT JOIN VideoLike vl ON v.videoNum = vl.video WHERE v.topic = :topic GROUP BY v.videoNum ORDER BY COUNT(vl) DESC")
    List<Video> findTopVideosByTopic(@Param("topic") Integer topic);

    @Query("SELECT v, COUNT(vl) as likeCount FROM Video v LEFT JOIN VideoLike vl ON v.videoNum = vl.video GROUP BY v.videoNum ORDER BY likeCount DESC")
    List<Object[]> findTopVideos();
}
