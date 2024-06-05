package com.chapssal.message.model;

import com.chapssal.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int num;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room")
    @JsonIgnore
    private ChatRoom room;

    private LocalDateTime joinDate;

    // Getter and Setter for isLeave
    @Setter
    @Getter
    private Boolean isLeave;

    // 기본 생성자
    public Participant() {
        this.isLeave = false;
    }

    public Participant(ChatRoom room, User user) {
        this.room = room;
        this.user = user;
        this.joinDate = LocalDateTime.now();
        this.isLeave = false;
    }

}