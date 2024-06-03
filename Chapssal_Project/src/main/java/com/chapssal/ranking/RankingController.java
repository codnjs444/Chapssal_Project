package com.chapssal.ranking;

import com.chapssal.util.MonthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @GetMapping
    public String showRankingPage(Model model) {
        // 현재 날짜를 기준으로 집계할 달 계산
        LocalDate now = LocalDate.now();

        // MonthUtil을 사용하여 이전 달의 년/월을 문자열로 추가
        String lastMonthFormatted = MonthUtil.getPreviousMonthFormatted(now);
        model.addAttribute("lastMonth", lastMonthFormatted);

        // 랭킹 페이지에 필요한 데이터를 모델에 추가
        Map<String, SchoolRanking> schoolRankings = rankingService.getSchoolRankings(now);
        model.addAttribute("schoolRankings", schoolRankings);

        return "ranking";
    }
}
