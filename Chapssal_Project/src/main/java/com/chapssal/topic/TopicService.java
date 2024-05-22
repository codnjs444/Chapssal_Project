package com.chapssal.topic;

import com.chapssal.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public void save(Topic topic) {
        topicRepository.save(topic);
    }

    // 이번주에 유저가 이미 토픽을 등록했는지 확인하는 메서드
    public boolean hasRegisteredThisWeek(User user) {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59);
        List<Topic> topics = topicRepository.findByUserAndCreateDateBetween(user, startOfWeek, endOfWeek);
        return !topics.isEmpty();
    }

    // 모든 토픽 찾기
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }
}
