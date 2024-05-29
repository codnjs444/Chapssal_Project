package com.chapssal.topic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import com.chapssal.user.UserService;

public class TopicControllerTest {

    @InjectMocks
    private TopicController topicController;

    @Mock
    private TopicService topicService;

    @Mock
    private UserService userService;

    @Mock
    private SelectedTopicRepository selectedTopicRepository;

    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new ConcurrentModel();
    }

    @Test
    public void testAddCurrentWeekToModel() {
        // 특정 날짜를 설정 (예: 2024년 6월 3일)
        LocalDate fixedDate = LocalDate.of(2024, 6, 3);

        // LocalDate.now()를 모킹하여 특정 날짜를 반환하도록 설정
        try (var mockedStaticLocalDate = org.mockito.Mockito.mockStatic(LocalDate.class, invocation -> {
            if ("now".equals(invocation.getMethod().getName())) {
                return fixedDate;
            }
            return invocation.callRealMethod();
        })) {

            // expectedFormattedDate 계산
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int year = fixedDate.getYear();
            int month = fixedDate.getMonthValue();
            int week = fixedDate.get(weekFields.weekOfMonth());

            String expectedFormattedDate = year + "년 " + month + "월 " + week + "주차";

            // 모델에 값을 추가하는 메서드 호출
            topicController.addCurrentWeekToModel(model);

            // 모델에 추가된 값을 가져옴
            String actualFormattedDate = (String) model.getAttribute("currentWeek");

            // 콘솔에 출력
            System.out.println("Expected: " + expectedFormattedDate);
            System.out.println("Actual: " + actualFormattedDate);

            // 값이 일치하는지 확인
            assertEquals(expectedFormattedDate, actualFormattedDate);
        }
    }

    @Test
    public void testAddCurrentWeekToModelForCurrentDate() {
        // 현재 날짜를 설정
        LocalDate currentDate = LocalDate.now();

        // LocalDate.now()를 모킹하여 현재 날짜를 반환하도록 설정
        try (var mockedStaticLocalDate = org.mockito.Mockito.mockStatic(LocalDate.class, invocation -> {
            if ("now".equals(invocation.getMethod().getName())) {
                return currentDate;
            }
            return invocation.callRealMethod();
        })) {

            // expectedFormattedDate 계산
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int year = currentDate.getYear();
            int month = currentDate.getMonthValue();
            int week = currentDate.get(weekFields.weekOfMonth());

            String expectedFormattedDate = year + "년 " + month + "월 " + week + "주차";

            // 모델에 값을 추가하는 메서드 호출
            topicController.addCurrentWeekToModel(model);

            // 모델에 추가된 값을 가져옴
            String actualFormattedDate = (String) model.getAttribute("currentWeek");

            // 콘솔에 출력
            System.out.println("Current Date: " + currentDate);
            System.out.println("Expected: " + expectedFormattedDate);
            System.out.println("Actual: " + actualFormattedDate);

            // 값이 일치하는지 확인
            assertEquals(expectedFormattedDate, actualFormattedDate);
        }
    }
}
