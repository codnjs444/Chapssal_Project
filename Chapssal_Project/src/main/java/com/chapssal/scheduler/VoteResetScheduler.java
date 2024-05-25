package com.chapssal.scheduler;

import com.chapssal.topic.SelectedTopicRepository;
import com.chapssal.user.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VoteResetScheduler {

    private final UserService userService;
    private final SelectedTopicRepository selectedTopicRepository;

    public VoteResetScheduler(UserService userService, SelectedTopicRepository selectedTopicRepository) {
        this.userService = userService;
        this.selectedTopicRepository = selectedTopicRepository;
    }

    // cron 표현식 초 / 분 / 시 / 일 / 월 / 요일
    // 해당 시간에 스케줄링된 작업을 처리 : userService의 resetAllvotes 메서드 실행
    // 추가적으로 selectdtopic 테이블 초기화
    @Scheduled(cron = "0 20 17 * * FRI")
    @Transactional
    public void resetVotes() {
        userService.resetAllVotes();
        selectedTopicRepository.deleteAll();
    }
}

