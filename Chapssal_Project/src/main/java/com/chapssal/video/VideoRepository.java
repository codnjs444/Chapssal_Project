package com.chapssal.video;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chapssal.user.User;

public interface VideoRepository extends JpaRepository<Video, Integer> {
	int countByUser_UserNum(Integer userNum);
    List<Video> findByUserInOrderByVideoNumAsc(List<User> users);
    List<Video> findByUser_UserNum(Integer userNum);
    List<Video> findByUser_UserNumOrderByUploadDateDesc(Integer userNum);

    Optional<Video> findFirstByUser_UserNumAndVideoNumLessThanOrderByVideoNumDesc(int userNum, int videoNum);
    Optional<Video> findFirstByUser_UserNumAndVideoNumGreaterThanOrderByVideoNumAsc(int userNum, int videoNum);
}
