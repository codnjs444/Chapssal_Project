package com.chapssal;

import com.chapssal.topic.SelectedTopicService;
import com.chapssal.video.VideoService;
import com.chapssal.video.VideoService.VideoWithLikesAndComments;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

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
    public String viewHomePage(Model model) {
//        List<Object[]> topicsByVoteCount = selectedTopicService.findTopicsByVoteCount();
//        model.addAttribute("topicsByVoteCount", topicsByVoteCount);

        List<Object[]> topicsByVoteCount = selectedTopicService.getTopicsByVoteCountForLastWeek();
        model.addAttribute("topicsByVoteCount", topicsByVoteCount);
        
        List<VideoWithLikesAndComments> videosWithLikesAndComments = videoService.getAllVideosOrderedByLikes();
        model.addAttribute("videos", videosWithLikesAndComments);

        return "home"; // home.html로 매핑
    }
}
