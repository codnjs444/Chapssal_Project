package com.chapssal.message.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageNum;

    private int sender;
    private int receiver;
    private String text;
    private byte messageType;
    private String filePath;
    private LocalDateTime sendDate;
    private byte isRead;

    @ManyToOne
    @JoinColumn(name = "room")
    @JsonBackReference
    private ChatRoom chatRoom;

    @Transient
    private int roomNum; // Transient 필드 추가
}