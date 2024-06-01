package com.chapssal.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class WeekUtil {

    public static String getWeekOfMonthWithCarryOver(LocalDate date) {
        // 현재 날짜의 년, 월 계산
        int year = date.getYear();
        int month = date.getMonthValue();

        // 현재 월의 첫 번째 월요일을 찾음
        LocalDate firstMondayOfCurrentMonth = LocalDate.of(year, month, 1)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        // 현재 월의 첫 번째 월요일 전까지는 이전 월의 마지막 주차로 간주
        if (date.isBefore(firstMondayOfCurrentMonth)) {
            date = date.minusMonths(1);
            year = date.getYear();
            month = date.getMonthValue();
        }

        // 주차 계산
        int weekOfMonth = 1 + (int) (date.getDayOfMonth()) / 7;

        // 첫 번째 월요일 전까지는 이전 월의 마지막 주차로 간주
        if (date.isBefore(firstMondayOfCurrentMonth.plusWeeks(1))) {
            month = month - 1;
            if (month == 0) {
                month = 12;
                year = year - 1;
            }
            weekOfMonth = 4; // 이전 월의 마지막 주차
        }

        return year + "년 " + month + "월 " + weekOfMonth + "주차";
    }
}
