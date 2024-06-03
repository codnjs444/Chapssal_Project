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
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "noti")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notiNum")
    private Long notiNum;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userNum")
    private User user;

    @Column(name = "type")
    private NotificationType type;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "userNum")
    private User sender;

    @Column(name = "message")
    private String message;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
   
    
}
