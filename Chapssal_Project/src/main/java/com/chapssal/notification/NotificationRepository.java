package com.chapssal.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chapssal.user.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserAndIsReadFalse(User user);
    List<Notification> findByUser(User user);
}
