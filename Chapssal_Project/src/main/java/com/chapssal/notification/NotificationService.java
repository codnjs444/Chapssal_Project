package com.chapssal.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.chapssal.user.User;
import com.chapssal.video.Video;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Autowired
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    // 새로운 알림 생성 (video 정보를 추가로 받는 메서드)
    public Notification createNotification(User user, NotificationType type, User sender, String message, Video video) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setSender(sender);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setVideo(video); // 비디오 정보 설정
        Notification savedNotification = notificationRepository.save(notification);
        eventPublisher.publishEvent(new NotificationEvent(this, savedNotification)); // 이벤트 발행
        return savedNotification;
    }

    // 알림을 읽음으로 표시
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow();
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 읽지 않은 알림 조회
    public List<Notification> getUnreadNotificationsByUser(User user) {
        return notificationRepository.findByUserAndIsReadFalse(user);
    }

    // 특정 사용자의 모든 알림 조회
    public List<Notification> getNotificationsByUser(User user) {
        return notificationRepository.findByUser(user);
    }
}
