package com.chapssal.topic;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.chapssal.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Optional<Topic> findByTitle(String title);
    List<Topic> findByUserAndCreateDateBetween(User user, LocalDateTime startOfWeek, LocalDateTime endOfWeek);
}