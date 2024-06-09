package com.chapssal.topic;

import com.chapssal.user.User;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final SelectedTopicRepository selectedTopicRepository;

    public TopicService(TopicRepository topicRepository, SelectedTopicRepository selectedTopicRepository) {
        this.topicRepository = topicRepository;
        this.selectedTopicRepository = selectedTopicRepository;
    }

    public void save(Topic topic) {
        topicRepository.save(topic);
    }

    // 이번주에 유저가 이미 토픽을 등록했는지 확인하는 메서드
    // 테이블 구조 변경으로 해당 메서드는 더 이상 사용하지 않음
//    public boolean hasRegisteredThisWeek(User user) {
//        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
//        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59);
//        List<Topic> topics = topicRepository.findByUserAndCreateDateBetween(user, startOfWeek, endOfWeek);
//        return !topics.isEmpty();
//    }

    // 모든 토픽 찾기
    public List<Topic> findAll() {
        return topicRepository.findAll();
    }

    // 각 토픽에 대한 투표 수 계산
    public Map<Integer, Long> getVoteCountsForTopics() {
        List<SelectedTopic> selectedTopics = selectedTopicRepository.findAll();
        return selectedTopics.stream()
                .collect(Collectors.groupingBy(st -> st.getTopic().getTopicNum(), Collectors.counting()));
    }

    // 아이디로 토픽 찾기 메서드
    public Topic findById(Integer id) {
        Optional<Topic> topic = topicRepository.findById(id);
        return topic.orElse(null);
    }

    // 주제 검색
    public List<Topic> searchTopics(String keyword) {
        return topicRepository.findByTitleContainingIgnoreCaseOrderByCountDesc(keyword);
    }

    // 이번 주의 토픽만 가져오는 메서드
    public List<Topic> findThisWeekTopics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MAX);

        return topicRepository.findAll().stream()
                .filter(topic -> {
                    LocalDateTime topicDate = topic.getCreateDate(); // 토픽 생성 날짜를 가져온다고 가정
                    return !topicDate.isBefore(startOfWeek) && !topicDate.isAfter(endOfWeek);
                })
                .collect(Collectors.toList());
    }

    // 이번 주에 특정 타이틀의 토픽이 이미 존재하는지 확인하는 메서드
    public Optional<Topic> findTopicByTitleThisWeek(String title) {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59);
        List<Topic> topics = topicRepository.findByTitleAndCreateDateBetween(title, startOfWeek, endOfWeek);
        return topics.stream().findFirst();
    }

    // 자동완성을 위한 메서드
    public List<Topic> findTopTopicsThisWeekByTitle(String title) {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59);
        return topicRepository.findTop3ByTitleStartingWithIgnoreCaseAndCreateDateBetweenOrderByCountDesc(title, startOfWeek, endOfWeek);
    }

    // 해당 주의 가장 많은 count 순으로 3개 검색
    public List<Topic> findTopTopicsThisWeek() {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59);
        return topicRepository.findTop3ByCreateDateBetweenOrderByCountDesc(startOfWeek, endOfWeek);
    }


    // 이 부분부터 토픽 투표 자동 추천 부분
    // 투표 횟수에 따른 상위 3개 토픽 반환
    public List<Topic> findTopTopicsByVotes() {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59);

        return selectedTopicRepository.findAllByCreateDateBetween(startOfWeek, endOfWeek).stream()
                .collect(Collectors.groupingBy(SelectedTopic::getTopic, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Topic, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 투표 횟수에 따른 특정 query로 시작하는 상위 3개 토픽 반환
    public List<Topic> findTopTopicsByVotes(String query) {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59);

        return selectedTopicRepository.findAllByCreateDateBetween(startOfWeek, endOfWeek).stream()
                .filter(st -> st.getTopic().getTitle().startsWith(query))
                .collect(Collectors.groupingBy(SelectedTopic::getTopic, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Topic, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // 검색 결과를 가져오는 서비스 메서드
    public List<Topic> searchTopicsByVotes(String query) {
        LocalDateTime startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime endOfWeek = LocalDate.now().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)).atTime(23, 59, 59);

        return selectedTopicRepository.findAllByCreateDateBetween(startOfWeek, endOfWeek).stream()
                .filter(st -> st.getTopic().getTitle().contains(query))
                .collect(Collectors.groupingBy(SelectedTopic::getTopic, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Topic, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}