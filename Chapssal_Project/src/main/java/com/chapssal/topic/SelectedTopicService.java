package com.chapssal.topic;

import com.chapssal.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class SelectedTopicService {

    private final SelectedTopicRepository selectedTopicRepository;

    @Autowired
    public SelectedTopicService(SelectedTopicRepository selectedTopicRepository) {
        this.selectedTopicRepository = selectedTopicRepository;
    }

    public List<SelectedTopic> findByUser(User user) {
        return selectedTopicRepository.findByUser(user);
    }
    
    public List<Object[]> findTopicsByVoteCount() {
        return selectedTopicRepository.findTopicsByVoteCount();
    }
    
    public List<Object[]> getTopicsByVoteCountForLastWeek() {
        // 현재 날짜를 기준으로 이번 주 월요일 구하기
        LocalDate today = LocalDate.now();
        LocalDate thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 지난 주 월요일과 일요일 설정
        LocalDate startOfLastWeek = thisMonday.minusWeeks(1);
        LocalDate endOfLastWeek = startOfLastWeek.plusDays(6);

        // LocalDateTime으로 변환
        LocalDateTime startDateTime = startOfLastWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfLastWeek.atTime(LocalTime.MAX);

        return selectedTopicRepository.findTopicsByVoteCountForWeek(startDateTime, endDateTime);
    }

    public List<Object[]> getTopicsByVoteCountForWeek(LocalDateTime startDate, LocalDateTime endDate) {
        return selectedTopicRepository.findTopicsByVoteCountForWeek(startDate, endDate);
    }
}
