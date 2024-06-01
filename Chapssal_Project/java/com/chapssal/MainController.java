package com.chapssal;

import com.chapssal.topic.SelectedTopicService;
import com.chapssal.video.Video;
import com.chapssal.video.VideoService;
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

    @GetMapping("/")
    public String showMainPage(Model model) {
        List<Object[]> topicsByVoteCount = selectedTopicService.findTopicsByVoteCount();
        model.addAttribute("topicsByVoteCount", topicsByVoteCount);

        List<Video> videos = videoService.findAll();
        model.addAttribute("videos", videos);

        return "home"; // home.html 파일을 렌더링
    }
}