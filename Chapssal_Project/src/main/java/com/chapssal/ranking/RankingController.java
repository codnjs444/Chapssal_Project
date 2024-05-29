package com.chapssal.ranking;

import com.chapssal.util.MonthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    @GetMapping
    public String showRankingPage(Model model) {
        // 현재 날짜를 기준으로 이전 달 계산
        LocalDate now = LocalDate.now();

        // MonthUtil을 사용하여 이전 달의 년/월을 문자열로 추가
        String lastMonthFormatted = MonthUtil.getPreviousMonthFormatted(now);
        model.addAttribute("lastMonth", lastMonthFormatted);

        // 랭킹 페이지에 필요한 데이터를 모델에 추가합니다.
        // 예시:
        // model.addAttribute("rankings", rankingService.getRankings());

        return "ranking";
    }
}
