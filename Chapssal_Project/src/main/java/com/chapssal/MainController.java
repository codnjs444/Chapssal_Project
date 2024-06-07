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
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/user/login";  // 로그인 페이지로 리다이렉트
        }
        
        String username = authentication.getName();  // 로그인한 사용자의 이름 가져오기
        User user = userService.getUser(username);
        String schoolName = userService.getSchoolNameByUserId(username);
        String userName = userService.getUserNameByUserId(username);
        String bio = userService.getUserBioByUserId(username);
        String profilePictureUrl = user.getProfilePictureUrl();
        
        model.addAttribute("schoolName", schoolName);
        model.addAttribute("userName", userName);
        model.addAttribute("bio", bio);
        model.addAttribute("profilePictureUrl", profilePictureUrl); // 프로필 사진 URL 추가
        
        Integer userNum = userService.getUserNumByUserId(username);
        int followingCount = followService.countFollowingByUserNum(userNum);
        int followerCount = followService.countFollowerByUserNum(userNum);
        int videoCount = videoService.countVideosByUserNum(userNum);  // 게시글 수 추가
        
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("videoCount", videoCount); // 게시글 수 모델에 추가
        
        List<User> followingUsers = followService.getFollowingUsers(userNum);
        List<User> followerUsers = followService.getFollowerUsers(userNum);
        
        model.addAttribute("followingUsers", followingUsers);
        model.addAttribute("followerUsers", followerUsers);
        
        model.addAttribute("currentUserNum", userNum); // 현재 사용자의 userNum 추가
        
        List<Video> userVideos = videoService.getVideosByUserNum(userNum);
        model.addAttribute("userVideos", userVideos);

        model.addAttribute("topicsByVoteCount", topicsByVoteCount);
        model.addAttribute("videos", videosWithLikesAndComments);
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("currentWeek", currentWeek);

        return "home"; // home.html로 매핑
    }

}