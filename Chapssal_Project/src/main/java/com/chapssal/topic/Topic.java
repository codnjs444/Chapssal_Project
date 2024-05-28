package com.chapssal.topic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Topic")
@Getter
@Setter
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer topicNum; // 기본 키, 자동 증가

    @Column(length = 255, nullable = false)
    private String title; // 투표 주제, 비어 있을 수 없음

    @Column(nullable = false)
    private LocalDateTime createDate; // 투표 주제 생성 날짜, 비어 있을 수 없음

    // 테이블 구조 변경으로 해당 컬럼은 더 이상 사용하지 않음
//    @ManyToOne
//    @JoinColumn(name = "user", nullable = false)
//    private User user; // User 테이블의 userNum을 참조하는 외래 키, 비어 있을 수 없음

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int count; // 중복된 주제 등록 횟수
}