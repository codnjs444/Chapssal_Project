package com.chapssal.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chapssal.user.User;
import com.chapssal.video.Video;

public interface UsertagRepository extends JpaRepository<Usertag, Integer> {
    boolean existsByVideoAndUser(Video video, User user);
}
