package com.chapssal;

import com.chapssal.topic.SelectedTopicService;
import com.chapssal.user.User;
import com.chapssal.user.UserService;
import com.chapssal.video.VideoLikeService;
import com.chapssal.video.VideoService;
import com.chapssal.video.VideoService.VideoWithLikesAndComments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final VideoService videoService;
    private final SelectedTopicService selectedTopicService;
    @Autowired
    private VideoLikeService videoLikeService;
    
    private final UserService userService;
    
//    @ModelAttribute("topicsByVoteCount")
//    public List<Object[]> topicsByVoteCount() {
//        return selectedTopicService.findTopicsByVoteCount();
//    }
//    랜덤 순서 영상 출력
    
//    @GetMapping("/")
//    public String viewHomePage(@RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset, Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//            return "redirect:/user/login";  // 로그인 페이지로 리다이렉트
//        }
//        String currentUsername = authentication.getName();  // 로그인한 사용자의 이름 가져오기
//        Optional<User> currentUserOptional = userService.findByUserId2(currentUsername);
//        
//        User currentUser = currentUserOptional.get();
//        Integer currentUserNum = currentUser.getUserNum(); // 현재 로그인한 사용자의 userNum
//        
//        LocalDate today = LocalDate.now();
//        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
//        LocalDate startOfWeek = monday.minusWeeks(weekOffset + 1); // 저번 주 월요일
//        LocalDate endOfWeek = startOfWeek.plusDays(6); // 저번 주 일요일
//
//        LocalDateTime startDateTime = startOfWeek.atStartOfDay();
//        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX);
//
//        List<Object[]> topicsByVoteCount = selectedTopicService.getTopicsByVoteCountForWeek(startDateTime, endDateTime);
//        List<VideoWithLikesAndComments> videosWithLikesAndComments = videoService.getAllVideosOrderedByUploadDate();
//
//        Map<Integer, Boolean> likedVideos = new HashMap<>();
//        for (VideoService.VideoWithLikesAndComments videoWithLikes : videosWithLikesAndComments) {
//            boolean isLiked = videoLikeService.isLikedByUser(videoWithLikes.getVideo().getVideoNum(), currentUserNum);
//            likedVideos.put(videoWithLikes.getVideo().getVideoNum(), isLiked);
//        }
//        model.addAttribute("likedVideos", likedVideos);  // Add likedVideos map to the model
//        if (!currentUserOptional.isPresent()) {
//            return "redirect:/"; // 사용자가 없으면 홈으로 리다이렉트
//        }
//        
//        int weekOfMonth = (startOfWeek.getDayOfMonth() - 1) / 7 + 1;
//        String currentWeek = String.format("%d년 %02d월 %d주차",
//                startOfWeek.getYear(),
//                startOfWeek.getMonthValue(),
//                weekOfMonth);
//        
//        model.addAttribute("currentUserNum", currentUserNum); // 현재 로그인한 사용자의 userNum 추가
//        model.addAttribute("topicsByVoteCount", topicsByVoteCount);
//        model.addAttribute("videos", videosWithLikesAndComments);
//        model.addAttribute("weekOffset", weekOffset);
//        model.addAttribute("currentWeek", currentWeek);
//
//        return "home"; // home.html로 매핑
//    }
    @GetMapping("/")
    public String viewHomePage(@RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size,
                               @RequestParam(value = "topic", required = false) Integer topic,
                               Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/user/login";  // 로그인 페이지로 리다이렉트
        }
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
        LocalDateTime endDateTime = endOfWeek.atTime(LocalTime.MAX); // 여기서 수정

        List<Object[]> topicsByVoteCount = selectedTopicService.getTopicsByVoteCountForWeek(startDateTime, endDateTime);
        Page<VideoWithLikesAndComments> videoPage;
        if (topic != null) {
            videoPage = videoService.getVideosByTopicPaginated(topic, page, size);
        } else {
            videoPage = videoService.getAllVideosPaginated(page, size);
        }

        Map<Integer, Boolean> likedVideos = new HashMap<>();
        for (VideoService.VideoWithLikesAndComments videoWithLikes : videoPage.getContent()) {
            boolean isLiked = videoLikeService.isLikedByUser(videoWithLikes.getVideo().getVideoNum(), currentUserNum);
            likedVideos.put(videoWithLikes.getVideo().getVideoNum(), isLiked);
        }
        model.addAttribute("likedVideos", likedVideos);

        int weekOfMonth = (startOfWeek.getDayOfMonth() - 1) / 7 + 1;
        String currentWeek = String.format("%d년 %02d월 %d주차",
                startOfWeek.getYear(),
                startOfWeek.getMonthValue(),
                weekOfMonth);
        
        model.addAttribute("currentUserNum", currentUserNum); // 현재 로그인한 사용자의 userNum 추가
        model.addAttribute("topicsByVoteCount", topicsByVoteCount);
        model.addAttribute("videos", videoPage.getContent());
        model.addAttribute("totalPages", videoPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("currentWeek", currentWeek);

        return "home"; // home.html로 매핑
    }
} 