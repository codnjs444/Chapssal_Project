package com.chapssal.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chapssal.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @Autowired
    private final NotificationRepository notificationRepository;

    // 새로운 알림 생성
    public Notification createNotification(User user, NotificationType type, User sender, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setSender(sender);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now()); // createdAt 필드 설정
        return notificationRepository.save(notification);
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