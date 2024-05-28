package com.chapssal.topic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Optional<Topic> findByTitle(String title);
    // 테이블 구조 변경으로 더 이상 해당 메서드는 사용하지 않음
    // List<Topic> findByUserAndCreateDateBetween(User user, LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    List<Topic> findByTitleContainingIgnoreCaseOrderByCountDesc(String keyword);

    // 이번 주에 특정 타이틀의 토픽이 존재하는지 확인하는 메서드
    List<Topic> findByTitleAndCreateDateBetween(String title, LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    // 타이틀이름으로 검색, 상위 3개, 이번 주, 카운트순 내림차순 정렬
    List<Topic> findTop3ByTitleStartingWithIgnoreCaseAndCreateDateBetweenOrderByCountDesc(String title, LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    // 상위 3개, 이번 주, 카운트순 내림차순 정렬
    List<Topic> findTop3ByCreateDateBetweenOrderByCountDesc(LocalDateTime startOfWeek, LocalDateTime endOfWeek);

    // 투표 페이지에서 검색 처리
    List<Topic> findByTitleContainingAndCreateDateBetween(String title, LocalDateTime startDate, LocalDateTime endDate);
}