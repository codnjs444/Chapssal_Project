package com.chapssal;

import com.chapssal.topic.SelectedTopicService;
import com.chapssal.video.VideoService;
import com.chapssal.video.VideoService.VideoWithLikesAndComments;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final VideoService videoService;
    private final SelectedTopicService selectedTopicService;

//    @ModelAttribute("topicsByVoteCount")
//    public List<Object[]> topicsByVoteCount() {
//        return selectedTopicService.findTopicsByVoteCount();
//    }
//    랜덤 순서 영상 출력
    
    @GetMapping("/")
    public String viewHomePage(@RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/user/login";  // 로그인 페이지로 리다이렉트
        }
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate startOfWeek = monday.minusWeeks(weekOffset + 1); // 저번 주 월요일
        LocalDate endOfWeek = startOfWeek.plusDays(6); // 저번 주 일요일

        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);

        List<Object[]> topicsByVoteCount = selectedTopicService.getTopicsByVoteCountForWeek(startDateTime, endDateTime);
        List<VideoWithLikesAndComments> videosWithLikesAndComments = videoService.getAllVideosOrderedByUploadDate();

        int weekOfMonth = (startOfWeek.getDayOfMonth() - 1) / 7 + 1;
        String currentWeek = String.format("%d년 %02d월 %d주차",
                startOfWeek.getYear(),
                startOfWeek.getMonthValue(),
                weekOfMonth);

        model.addAttribute("topicsByVoteCount", topicsByVoteCount);
        model.addAttribute("videos", videosWithLikesAndComments);
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("currentWeek", currentWeek);

        return "home"; // home.html로 매핑
    }
//    @GetMapping("/")
//    public String viewHomePage(@RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset, 
//                               @RequestParam(value = "page", defaultValue = "0") int page,
//                               @RequestParam(value = "size", defaultValue = "3") int size, 
//                               Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//            return "redirect:/user/login";  // 로그인 페이지로 리다이렉트
//        }
//        LocalDate today = LocalDate.now();
//        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
//        LocalDate startOfWeek = monday.minusWeeks(weekOffset + 1); // 저번 주 월요일
//        LocalDate endOfWeek = startOfWeek.plusDays(6); // 저번 주 일요일
//
//        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
//        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);
//
//        List<Object[]> topicsByVoteCount = selectedTopicService.getTopicsByVoteCountForWeek(startDateTime, endDateTime);
//        List<VideoWithLikesAndComments> videosWithLikesAndComments = videoService.getVideosWithLikeAndCommentCountsPaged(page, size);
//
//        int weekOfMonth = (startOfWeek.getDayOfMonth() - 1) / 7 + 1;
//        String currentWeek = String.format("%d년 %02d월 %d주차",
//                startOfWeek.getYear(),
//                startOfWeek.getMonthValue(),
//                weekOfMonth);
//
//        model.addAttribute("topicsByVoteCount", topicsByVoteCount);
//        model.addAttribute("videos", videosWithLikesAndComments);
//        model.addAttribute("weekOffset", weekOffset);
//        model.addAttribute("currentWeek", currentWeek);
//        model.addAttribute("currentPage", page);
//
//        return "home"; // home.html로 매핑
//    }
}