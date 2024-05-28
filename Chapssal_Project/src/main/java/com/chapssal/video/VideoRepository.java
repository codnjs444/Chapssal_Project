package com.chapssal.video;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Integer> {
	int countByUser_UserNum(Integer userNum);
}
