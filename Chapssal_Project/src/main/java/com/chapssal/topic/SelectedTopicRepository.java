package com.chapssal.topic;

import com.chapssal.user.User;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SelectedTopicRepository extends JpaRepository<SelectedTopic, Integer> {
    List<SelectedTopic> findByUser(User user);
    List<SelectedTopic> findByTopic(Topic topic);
    List<SelectedTopic> findAllByCreateDateBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT st.topic, COUNT(st.topic) as topicCount FROM SelectedTopic st GROUP BY st.topic ORDER BY topicCount DESC LIMIT 10")
    List<Object[]> findTopicsByVoteCount();
    

    @Query("SELECT st.topic, COUNT(st.topic) as topicCount FROM SelectedTopic st " +
            "WHERE st.createDate >= :startDate AND st.createDate < :endDate " +
            "GROUP BY st.topic ORDER BY topicCount DESC LIMIT 10")
    List<Object[]> findTopicsByVoteCountForWeek(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}