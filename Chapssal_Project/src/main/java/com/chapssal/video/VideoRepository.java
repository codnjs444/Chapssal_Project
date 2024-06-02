package com.chapssal.video;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {
	int countByUser_UserNum(Integer userNum);
	List<Video> findByUser_School_SchoolNameAndUploadDateBetween(String schoolName, LocalDateTime startDate, LocalDateTime endDate);
}
