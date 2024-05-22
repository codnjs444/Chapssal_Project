package com.chapssal.topic;

import com.chapssal.user.User;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectedTopicRepository extends JpaRepository<SelectedTopic, Integer> {
    List<SelectedTopic> findByUser(User user);
    List<SelectedTopic> findByTopic(Topic topic);
}