package com.chapssal.topic;

import com.chapssal.user.User;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SelectedTopicRepository extends JpaRepository<SelectedTopic, Integer> {
    List<SelectedTopic> findByUser(User user);
    List<SelectedTopic> findByTopic(Topic topic);
    
    @Query("SELECT st.topic, COUNT(st.topic) as topicCount FROM SelectedTopic st GROUP BY st.topic ORDER BY topicCount DESC LIMIT 10")
    List<Object[]> findTopicsByVoteCount();
}