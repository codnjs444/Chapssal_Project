//package com.chapssal.util;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.time.LocalDate;
//
//public class WeekUtilTest {
//
//    @BeforeEach
//    public void setUp( ) {
//
//    }
//
//    @Test
//    public void testGetWeekOfMonthWithCarryOver() {
//        // 특정 날짜를 설정
//        LocalDate date1 = LocalDate.of(2024, 6, 2);
//        LocalDate date2 = LocalDate.of(2024, 6, 3);
//
//        // 기대 결과
//        String expectedWeek1 = "2024년 5월 4주차";
//        String expectedWeek2 = "2024년 6월 1주차";
//
//        // 실제 결과
//        String actualWeek1 = WeekUtil.getWeekOfMonthWithCarryOver(date1);
//        String actualWeek2 = WeekUtil.getWeekOfMonthWithCarryOver(date2);
//
//        // 결과 확인
//        assertEquals(expectedWeek1, actualWeek1);
//        assertEquals(expectedWeek2, actualWeek2);
//    }
//
//    @Test
//    public void testGetWeekOfMonthWithCarryOverForCurrentDate() {
//        // 현재 날짜를 설정
//        LocalDate currentDate = LocalDate.now();
//
//        // 실제 결과
//        String actualWeek = WeekUtil.getWeekOfMonthWithCarryOver(currentDate);
//
//        // 결과 출력 (콘솔 출력으로 확인)
//        System.out.println("Current Date: " + currentDate);
//        System.out.println("Calculated Week: " + actualWeek);
//
//        // 결과 확인 (여기서는 특정 값을 확인하지 않고, 올바른 형식의 문자열이 반환되는지 확인)
//        assertTrue(actualWeek.matches("\\d{4}년 \\d{1,2}월 \\d주차"));
//    }
//}
