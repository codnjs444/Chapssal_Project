package com.chapssal.video;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chapssal.notification.NotificationService;
import com.chapssal.notification.NotificationType;
import com.chapssal.user.User;

import jakarta.transaction.Transactional;

@Service
public class VideoLikeService {
    @Autowired
    private VideoLikeRepository videoLikeRepository;
    @Autowired
    private NotificationService notificationService;

    public void likeVideo(Video video, User user) {
        VideoLike videoLike = new VideoLike();
        videoLike.setVideo(video);
        videoLike.setUser(user);
        videoLike.setLikeDate(LocalDateTime.now());
        videoLikeRepository.save(videoLike);

        // 알림 생성
        String message = user.getUserName() + "님이 " + video.getTitle() + " 영상에 좋아요를 눌렀습니다.";
        notificationService.createNotification(video.getUser(), NotificationType.LIKE_VIDEO, user, message, video);
    }
    
    public boolean isLikedByUser(Integer videoId, Integer userId) {
        Video video = new Video();
        video.setVideoNum(videoId);
        User user = new User();
        user.setUserNum(userId);
        return videoLikeRepository.existsByVideoAndUser(video, user);
    }
    
    public int countLikesByVideoId(Integer videoId) {
        Video video = new Video();
        video.setVideoNum(videoId);
        return videoLikeRepository.countByVideo(video);
    }

    public List<VideoLike> findByVideoAndUser(int videoId, int userId) {
        Video video = new Video();
        video.setVideoNum(videoId);
        User user = new User();
        user.setUserNum(userId);
        return videoLikeRepository.findByVideoAndUser(video, user);
    }

    @Transactional // 트랜잭션 활성화
    public void deleteByVideoAndUser(int videoId, int userId) {
        Video video = new Video();
        video.setVideoNum(videoId);
        User user = new User();
        user.setUserNum(userId);
        videoLikeRepository.deleteByVideoAndUser(video, user);
    }
}