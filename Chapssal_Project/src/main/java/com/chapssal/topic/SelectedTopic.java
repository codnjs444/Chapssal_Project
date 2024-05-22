package com.chapssal.topic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.chapssal.user.User;

@Entity
@Table(name = "SelectedTopic")
@Getter
@Setter
public class SelectedTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer selectNum;

    @ManyToOne
    @JoinColumn(name = "topic", nullable = false)
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private User user;
}