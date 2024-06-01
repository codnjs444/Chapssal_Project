package com.chapssal.comment;

import com.chapssal.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rcommentNum;
    
    @ManyToOne
    @JoinColumn(name = "comment", referencedColumnName = "commentNum")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "userNum")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime date;
}
