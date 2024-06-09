package com.chapssal.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MonthUtil {

    /**
     * 주어진 날짜를 기준으로 전월의 년/월을 반환하는 메서드
     * @param date 기준 날짜
     * @return 전월의 년/월을 포맷팅한 문자열
     */
    public static String getPreviousMonthFormatted(LocalDate date) {
        LocalDate lastMonth = date.minusMonths(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return lastMonth.format(formatter);
    }
}