package com.chapssal;

import com.chapssal.topic.SelectedTopicService;
import com.chapssal.video.Video;
import com.chapssal.video.VideoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final VideoService videoService;
    private final SelectedTopicService selectedTopicService;

    @ModelAttribute("topicsByVoteCount")
    public List<Object[]> topicsByVoteCount() {
        return selectedTopicService.findTopicsByVoteCount();
    }
    
    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Object[]> topicsByVoteCount = selectedTopicService.findTopicsByVoteCount();
        model.addAttribute("topicsByVoteCount", topicsByVoteCount);

        List<VideoService.VideoWithLikesAndComments> videosWithLikesAndComments = videoService.getAllVideosWithLikeAndCommentCounts();
        Collections.shuffle(videosWithLikesAndComments); // 영상 리스트를 랜덤으로 섞음
        model.addAttribute("videos", videosWithLikesAndComments);

        return "home"; // home.html로 매핑
    }
}
