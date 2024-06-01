package com.chapssal.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VideoLikeService {
    @Autowired
    private VideoLikeRepository videoLikeRepository;

    public boolean isLikedByUser(Integer videoId, Integer userId) {
        return videoLikeRepository.existsByVideoAndUser(videoId, userId);
    }
    
    public int countLikesByVideoId(Integer videoId) {
        return videoLikeRepository.countByVideoId(videoId);
    }
}
