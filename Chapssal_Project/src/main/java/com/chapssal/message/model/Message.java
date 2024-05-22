package com.chapssal.message.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}