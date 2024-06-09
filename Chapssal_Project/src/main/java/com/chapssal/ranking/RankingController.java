package com.chapssal.ranking;

import com.chapssal.topic.SelectedTopicService;
import com.chapssal.util.MonthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;
    
    @Autowired
    private SelectedTopicService selectedTopicService;

    @ModelAttribute("topicsByVoteCount")
    public List<Object[]> topicsByVoteCount() {
//        return selectedTopicService.findTopicsByVoteCount();
    	return selectedTopicService.getTopicsByVoteCountForLastWeek();
    }

    @GetMapping
    public String showRankingPage(Model model, @RequestParam(value = "search", required = false) String search,
                                  @RequestParam(value = "date", required = false) String dateString) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/user/login";  // 로그인 페이지로 리다이렉트
        }
        YearMonth date;
        if (dateString != null) {
            date = YearMonth.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM"));
        } else {
            date = YearMonth.now().minusMonths(1);
        }

        String currentMonthFormatted = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        model.addAttribute("currentMonth", currentMonthFormatted);

        LocalDate endOfMonth = date.atEndOfMonth();
        Map<String, SchoolRanking> schoolRankings;
        if (search != null && !search.isEmpty()) {
            schoolRankings = rankingService.searchSchoolRankings(endOfMonth, search);
        } else {
            schoolRankings = rankingService.getSchoolRankings(endOfMonth);
        }

        model.addAttribute("schoolRankings", schoolRankings);

        return "ranking";
    }
}