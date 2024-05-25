package com.chapssal.notification;

import java.time.LocalDateTime;

import com.chapssal.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notiNum;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userNum")
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "userNum")
    private User sender;

    private String message;

    private boolean isRead;

    private LocalDateTime createdAt = LocalDateTime.now();
}
