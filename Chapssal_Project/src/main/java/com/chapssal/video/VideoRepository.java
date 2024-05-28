package com.chapssal.video;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Integer> {
	int countByUser_UserNum(Integer userNum);
	List<Video> findByUser_UserNum(Integer userNum);
}
