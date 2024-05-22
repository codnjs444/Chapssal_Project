package com.chapssal.topic;

import com.chapssal.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.chapssal.topic.TopicService;
import com.chapssal.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/topic")
public class TopicController {
    private final TopicService topicService;
    private final UserService userService;

    public  TopicController(TopicService topicService, UserService userService){
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping("/input")
    public String showTopicInputForm(Model model) {
        model.addAttribute("topic", new Topic());
        return "topic_input";
    }

    // 주제 등록 로직은 여기서 처리
    @PostMapping("/register")
    public String registerTopic(@ModelAttribute Topic topic, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "topic_input";
        }

        // Security에서 사용자 인증 받아오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 이거 아이디 가져오는 거임 -> username 은 user 엔티티의 userId임
        User user = userService.findByUserId(username);

        // 사용자가 널이 아니면 토픽 저장
        if (user != null) {
            // 이번주 토픽을 이미 등록했는지 확인
            if (topicService.hasRegisteredThisWeek(user)) {
                model.addAttribute("errorMessage", "이미 이번 주 토픽을 등록하셨습니다.");
            } else {
                topic.setUser(user);
                topic.setCreateDate(LocalDateTime.now());
                topicService.save(topic);
                model.addAttribute("successMessage", "토픽 등록이 완료되었습니다.");
            }
        }

        return "topic_input"; // 등록 후 리다이렉트할 페이지
    }

    // 투표 페이지 컨트롤러
    @GetMapping("/vote")
    public String showVotePage(Model model) {
        List<Topic> topics = topicService.findAll();
        model.addAttribute("topics", topics);
        return "vote_page";
    }

}