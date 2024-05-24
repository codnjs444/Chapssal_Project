package com.chapssal;

import com.chapssal.topic.Topic;
import com.chapssal.topic.TopicService;
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
    private final TopicService topicService;

    @GetMapping("/")
    public String showMainPage(Model model) {
        List<Topic> topics = topicService.findAll();
        model.addAttribute("topics", topics);

    	
        List<Video> videos = videoService.findAll(); // 인스턴스 메소드 호출
        model.addAttribute("videos", videos);
        return "home"; // home.html 파일을 렌더링
    }
}
