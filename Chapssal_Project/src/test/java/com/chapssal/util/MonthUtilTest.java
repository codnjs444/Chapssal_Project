package com.chapssal.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class MonthUtilTest {

    @Test
    public void testGetPreviousMonthFormatted() {
        // 특정 날짜를 설정 (예: 2024년 5월 29일, 6월 2일, 6월 3일)
        LocalDate date1 = LocalDate.of(2024, 5, 29);
        LocalDate date2 = LocalDate.of(2024, 6, 2);
        LocalDate date3 = LocalDate.of(2024, 6, 3);
        LocalDate date4 = LocalDate.of(2024, 2, 4);
        LocalDate date5 = LocalDate.of(2024, 2, 5);

        // 기대 결과
        String expectedWeek1 = "24년 4월";
        String expectedWeek2 = "24년 4월";
        String expectedWeek3 = "24년 5월";
        String expectedWeek4 = "23년 12월";
        String expectedWeek5 = "24년 1월";

        // 실제 결과
        String actualWeek1 = MonthUtil.getPreviousMonthFormatted(date1);
        String actualWeek2 = MonthUtil.getPreviousMonthFormatted(date2);
        String actualWeek3 = MonthUtil.getPreviousMonthFormatted(date3);
        String actualWeek4 = MonthUtil.getPreviousMonthFormatted(date4);
        String actualWeek5 = MonthUtil.getPreviousMonthFormatted(date5);

        // 콘솔 출력
        System.out.println("Date: " + date1 + " - Expected: " + expectedWeek1 + ", Actual: " + actualWeek1);
        System.out.println("Date: " + date2 + " - Expected: " + expectedWeek2 + ", Actual: " + actualWeek2);
        System.out.println("Date: " + date3 + " - Expected: " + expectedWeek3 + ", Actual: " + actualWeek3);
        System.out.println("Date: " + date4 + " - Expected: " + expectedWeek4 + ", Actual: " + actualWeek4);
        System.out.println("Date: " + date5 + " - Expected: " + expectedWeek5 + ", Actual: " + actualWeek5);

        // 결과 확인
        assertEquals(expectedWeek1, actualWeek1);
        assertEquals(expectedWeek2, actualWeek2);
        assertEquals(expectedWeek3, actualWeek3);
        assertEquals(expectedWeek4, actualWeek4);
        assertEquals(expectedWeek5, actualWeek5);
    }

    @Test
    public void testGetPreviousMonthFormattedForCurrentDate() {
        // 현재 날짜를 설정
        LocalDate currentDate = LocalDate.now();

        // 실제 결과
        String actualWeek = MonthUtil.getPreviousMonthFormatted(currentDate);

        // 결과 출력 (콘솔 출력으로 확인)
        System.out.println("Current Date: " + currentDate);
        System.out.println("Calculated Previous Month: " + actualWeek);

        // 결과 확인 (여기서는 특정 값을 확인하지 않고, 올바른 형식의 문자열이 반환되는지 확인)
        assertTrue(actualWeek.matches("\\d{2}년 \\d{1,2}월"));
    }
}
