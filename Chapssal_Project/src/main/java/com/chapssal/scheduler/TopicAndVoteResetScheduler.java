package com.chapssal.scheduler;

import com.chapssal.topic.SelectedTopicRepository;
import com.chapssal.user.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TopicAndVoteResetScheduler {

    private final UserService userService;
    private final SelectedTopicRepository selectedTopicRepository;

    public TopicAndVoteResetScheduler(UserService userService, SelectedTopicRepository selectedTopicRepository) {
        this.userService = userService;
        this.selectedTopicRepository = selectedTopicRepository;
    }

    // cron 표현식 초 / 분 / 시 / 일 / 월 / 요일
    // 해당 시간에 스케줄링된 작업을 처리 : userService의 resetAllvotes 메서드 실행
    // 추가적으로 selectedtopic 테이블 초기화
    @Scheduled(cron = "0 20 17 * * FRI")
    @Transactional
    public void resetVotes() {
        // 모든 유저의 topic / vote 철럼 초기화
        userService.resetAllTopics();
        userService.resetAllVotes();

        // 모든 selectedTopic table 삭제
        // 해당 메서드 실행하지 않도록 변경
        // selectedTopicRepository.deleteAll();
    }
}

