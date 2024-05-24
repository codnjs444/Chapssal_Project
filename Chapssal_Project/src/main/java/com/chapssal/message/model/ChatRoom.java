package com.chapssal.message.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomNum;

    private String title;
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "chatRoom")
    @JsonManagedReference
    private List<Message> messages;

    @OneToMany(mappedBy = "room")
    private List<Participant> participants;
}