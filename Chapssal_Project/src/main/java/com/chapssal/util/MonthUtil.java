package com.chapssal.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class MonthUtil {

    /**
     * 주어진 날짜를 기준으로 이전 달의 년/월을 반환하는 메서드
     * @param date 기준 날짜
     * @return 이전 달의 년/월을 포맷팅한 문자열
     */
    public static String getPreviousMonthFormatted(LocalDate date) {
        LocalDate firstDayOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);

        // 첫 주의 경우 전전달을 반환
        if (firstDayOfWeek.isBefore(firstDayOfMonth)) {
            LocalDate previousMonth = date.minusMonths(2); // 전전달
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 M월");
            return previousMonth.format(formatter);
        } else {
            LocalDate lastMonth = date.minusMonths(1); // 이전 달
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 M월");
            return lastMonth.format(formatter);
        }
    }
}
