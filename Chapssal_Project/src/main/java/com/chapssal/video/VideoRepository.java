package com.chapssal.video;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    int countByUser_UserNum(Integer userNum);
    List<Video> findByUser_UserNum(Integer userNum);
    List<Video> findByUser_UserNumOrderByUploadDateDesc(Integer userNum);

    Optional<Video> findFirstByUser_UserNumAndVideoNumLessThanOrderByVideoNumDesc(int userNum, int videoNum);
    Optional<Video> findFirstByUser_UserNumAndVideoNumGreaterThanOrderByVideoNumAsc(int userNum, int videoNum);

    List<Video> findByTitleContaining(String title);
}
