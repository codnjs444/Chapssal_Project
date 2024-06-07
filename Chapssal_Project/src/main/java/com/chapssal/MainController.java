package com.chapssal;

import com.chapssal.award.Award;
import com.chapssal.award.AwardService;
import com.chapssal.follow.FollowService;
import com.chapssal.school.SchoolRepository;
import com.chapssal.topic.SelectedTopicService;
import com.chapssal.user.User;
import com.chapssal.user.UserService;
import com.chapssal.video.Video;
import com.chapssal.video.VideoService;
import com.chapssal.video.VideoService.VideoWithLikesAndComments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // 추가 필요

import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class MainController {

    @Autowired
    private final UserService userService;
	
    @Autowired
    private FollowService followService;

    @Autowired
    private final VideoService videoService;
    
    @Autowired
    private AwardService awardService;
    
    @Autowired
    private SelectedTopicService selectedTopicService;
//    @ModelAttribute("topicsByVoteCount")
//    public List<Object[]> topicsByVoteCount() {
//        return selectedTopicService.findTopicsByVoteCount();
//    }
//    랜덤 순서 영상 출력
    
    @GetMapping("/")
    public String viewHomePage(@RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset, Model model) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";  // 로그인 페이지로 리다이렉트
        }
        
        // 현재 로그인한 사용자의 정보를 가져옴
        String currentUsername = authentication.getName();  // 로그인한 사용자의 이름 가져오기
        Optional<User> currentUserOptional = userService.findByUserId2(currentUsername);
        
        if (!currentUserOptional.isPresent()) {
            return "redirect:/"; // 사용자가 없으면 홈으로 리다이렉트
        }
        
        User currentUser = currentUserOptional.get();
        Integer currentUserNum = currentUser.getUserNum(); // 현재 로그인한 사용자의 userNum
        
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
        model.addAttribute("currentUserNum", currentUserNum); // currentUserNum을 모델에 추가

        return "home"; // home.html로 매핑
    }

    @GetMapping("/checkFollowStatus")
    @ResponseBody
    public boolean checkFollowStatus(@RequestParam("currentUserNum") Integer currentUserNum,
                                     @RequestParam("userNum") Integer userNum) {
        return followService.isFollowing(currentUserNum, userNum);
    }

}