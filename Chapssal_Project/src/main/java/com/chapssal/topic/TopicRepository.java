package com.chapssal.topic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.chapssal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Optional<Topic> findByTitle(String title);
    // 테이블 구조 변경으로 더 이상 해당 메서드는 사용하지 않음
    // List<Topic> findByUserAndCreateDateBetween(User user, LocalDateTime startOfWeek, LocalDateTime endOfWeek);
    List<Topic> findByTitleContainingIgnoreCaseOrderByCountDesc(String keyword);

    // 이번 주에 특정 타이틀의 토픽이 존재하는지 확인하는 메서드
    List<Topic> findByTitleAndCreateDateBetween(String title, LocalDateTime startOfWeek, LocalDateTime endOfWeek);
}