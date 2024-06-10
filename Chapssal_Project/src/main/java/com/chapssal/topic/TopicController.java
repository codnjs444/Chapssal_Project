package com.chapssal.topic;

import com.chapssal.user.UserService;
import com.chapssal.topic.SelectedTopicService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.chapssal.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/topic")
public class TopicController {
    private final TopicService topicService;
    private final UserService userService;
    private final SelectedTopicService selectedTopicService;
    private final SelectedTopicRepository selectedTopicRepository;

    public TopicController(TopicService topicService, UserService userService, SelectedTopicService selectedTopicService, SelectedTopicRepository selectedTopicRepository) {
        this.topicService = topicService;
        this.userService = userService;
        this.selectedTopicService = selectedTopicService;
        this.selectedTopicRepository = selectedTopicRepository;
    }

    // 현재 년/월/주차를 계산해서 모델에 추가하는 메서드
    // 테스트 클래스에서 사용하기 위해 접근 제한자를 private에서 protected로 변경
    protected void addCurrentWeekToModel(Model model) {
        LocalDate now = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = now.getYear();
        int month = now.getMonthValue();
        int week = now.get(weekFields.weekOfMonth());
        String formattedDate = year + "년 " + month + "월 " + week + "주차";
        model.addAttribute("currentWeek", formattedDate);
    }

    // 현재 시간에 따라 topic에 접근시 어떤 페이지를 리턴할지 정하는 메서드
    // 테스트용이므로 임시로 현재 시간의 분의 끝자리에 따라 어떤 페이지를 리턴할지 결정
    @GetMapping
    public String routeToPage(Model model) {
        int minute = LocalDateTime.now().get(ChronoField.MINUTE_OF_HOUR);
        if (minute % 10 <= 3) {
            return showTopicInputForm(model);
        } else if (minute % 10 <= 6) {
            return showVotePage(model);
        } else {
            return showResultsPage(model);
        }
    }

    // 토픽 입력 페이지 출력 메서드
    @GetMapping("/input")
    public String showTopicInputForm(Model model) {
        model.addAttribute("topic", new Topic());
        addCurrentWeekToModel(model);
        return "topic_input";
    }

    // 토픽 등록 처리 로직 메서드
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
            // 테이블 구조 변경으로 토픽 등록 메서드 수정
//            // 이번주 토픽을 이미 등록했는지 확인
//            if (topicService.hasRegisteredThisWeek(user)) {
//                model.addAttribute("errorMessage", "이미 이번 주 토픽을 등록하셨습니다.");
//            } else {
//                topic.setUser(user);
//                topic.setCreateDate(LocalDateTime.now());
//                topicService.save(topic);
//                model.addAttribute("successMessage", "토픽 등록이 완료되었습니다.");
//            }
            if (user.getTopic() >= 1) {
                model.addAttribute("errorMessage", "이미 이번 주 토픽을 등록하셨습니다.");
            } else {
                // 이번 주에 동일한 타이틀의 토픽이 있는지 확인
                Optional<Topic> existingTopic = topicService.findTopicByTitleThisWeek(topic.getTitle());
                if (existingTopic.isPresent()) {
                    // 이미 존재하면 count 값 증가
                    Topic foundTopic = existingTopic.get();
                    foundTopic.setCount(foundTopic.getCount() + 1);
                    topicService.save(foundTopic);
                    model.addAttribute("successMessage", "토픽 등록이 완료되었습니다.");

                    // SelectedTopic에 추가
                    SelectedTopic selectedTopic = new SelectedTopic();
                    selectedTopic.setTopic(foundTopic);
                    selectedTopic.setUser(user);
                    selectedTopic.setCreateDate(LocalDateTime.now());
                    selectedTopicService.save(selectedTopic);
                } else {
                    // 존재하지 않으면 새로 등록
                    topic.setCreateDate(LocalDateTime.now());
                    topic.setCount(1); // 처음 등록이므로 count를 1로 설정
                    topicService.save(topic);
                    model.addAttribute("successMessage", "토픽 등록이 완료되었습니다.");

                    // SelectedTopic에 추가
                    SelectedTopic selectedTopic = new SelectedTopic();
                    selectedTopic.setTopic(topic);
                    selectedTopic.setUser(user);
                    selectedTopic.setCreateDate(LocalDateTime.now());
                    selectedTopicService.save(selectedTopic);
                }

                // 유저 테이블의 topic 컬럼 값을 1 증가
                user.setTopic(user.getTopic() + 1);
                user.setVote(user.getVote() + 1);
                userService.save(user);
            }
        }
        addCurrentWeekToModel(model);

        return "topic_input"; // 등록 후 리다이렉트할 페이지
    }

    // 투표 페이지 리턴 메서드
    @GetMapping("/vote")
    public String showVotePage(Model model) {
        List<Topic> topics = topicService.findThisWeekTopics();
        Map<Integer, Long> voteCounts = topicService.getVoteCountsForTopics();

        // 토픽 목록을 투표 횟수에 따라 내림차순으로 정렬
        List<Topic> sortedTopics = topics.stream()
                .sorted((t1, t2) -> voteCounts.getOrDefault(t2.getTopicNum(), 0L).compareTo(voteCounts.getOrDefault(t1.getTopicNum(), 0L)))
                .collect(Collectors.toList());

        // 현재 사용자가 투표한 토픽 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserId(username);

        Set<Integer> votedTopicIds = selectedTopicRepository.findByUser(user).stream()
                .map(selectedTopic -> selectedTopic.getTopic().getTopicNum())
                .collect(Collectors.toSet());

        addCurrentWeekToModel(model);
        model.addAttribute("topics", sortedTopics);
        model.addAttribute("voteCounts", voteCounts);
        model.addAttribute("votedTopicIds", votedTopicIds);
        model.addAttribute("userVoteCount", user.getVote()); // 유저의 이번 주 투표횟수
        model.addAttribute("maxVoteCount", 5); // 최대 투표 가능 횟수
        return "vote_page";
    }

    // 투표 버튼 클릭 메서드
    @PostMapping("/select")
    @ResponseBody
    public ResponseEntity<String> selectTopic(@RequestParam(name = "topicId") Integer topicId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUserId(username);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }

            if (userService.hasReachedVoteLimit(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("이번주 투표 가능한 횟수 5회를 모두 사용하셨습니다.");
            }

            Topic topic = topicService.findById(topicId);
            if (topic == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 토픽입니다.");
            }

            SelectedTopic selectedTopic = new SelectedTopic();
            selectedTopic.setTopic(topic);
            selectedTopic.setUser(user);
            selectedTopic.setCreateDate(LocalDateTime.now()); // 현재 시간으로 등록
            selectedTopicRepository.save(selectedTopic);

            // 투표 횟수 증가
            userService.incrementVote(user);

            return ResponseEntity.ok("투표가 완료되었습니다.");
        } catch (Exception e) {
            // 예외를 잡아서 로그에 기록
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
        }
    }


    // 결과 페이지 리턴 메서드
    @GetMapping("/results")
    public String showResultsPage(Model model) {
        List<Topic> topics = topicService.findThisWeekTopics();
        Map<Integer, Long> voteCounts = topicService.getVoteCountsForTopics();

        // 토픽 목록을 투표 횟수에 따라 내림차순으로 정렬하고 상위 10개만 추출
        List<Topic> sortedTopics = topics.stream()
                .sorted((t1, t2) -> voteCounts.getOrDefault(t2.getTopicNum(), 0L).compareTo(voteCounts.getOrDefault(t1.getTopicNum(), 0L)))
                .limit(10)
                .collect(Collectors.toList());

        addCurrentWeekToModel(model);
        model.addAttribute("topics", sortedTopics);
        model.addAttribute("voteCounts", voteCounts);
        return "vote_results_page";
    }

    // AJAX 요청을 처리하는 메서드
    // 토픽 입력 자동 추천
    @GetMapping("/topicsuggestions")
    @ResponseBody
    public List<Topic> getSuggestions(@RequestParam(name = "query", required = false) String query) {
        if (query == null || query.isEmpty()) {
            return topicService.findTopTopicsThisWeek();
        } else {
            return topicService.findTopTopicsThisWeekByTitle(query);
        }
    }

    // AJAX 요청을 처리하는 메서드
    // 토픽 투표 자동 추천
    @GetMapping("/votesuggestions")
    @ResponseBody
    public List<Topic> getVoteSuggestions(@RequestParam(name = "query",required = false) String query) {
        if (query == null || query.isEmpty()) {
            return topicService.findTopTopicsByVotes();
        } else {
            return topicService.findTopTopicsByVotes(query);
        }
    }

    // 검색 버튼 이벤트 구현 메서드
    @GetMapping("/search")
    @ResponseBody
    public List<Map<String, Object>> searchTopics(@RequestParam(name = "query",required = false) String query) {
        List<Topic> topics = topicService.searchTopicsByVotes(query);
        Map<Integer, Long> voteCounts = topicService.getVoteCountsForTopics();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserId(username);

        Set<Integer> votedTopicIds = selectedTopicRepository.findByUser(user).stream()
                .map(selectedTopic -> selectedTopic.getTopic().getTopicNum())
                .collect(Collectors.toSet());

        return topics.stream().map(topic -> {
            Map<String, Object> result = new HashMap<>();
            result.put("topicNum", topic.getTopicNum());
            result.put("title", topic.getTitle());
            result.put("voteCount", voteCounts.getOrDefault(topic.getTopicNum(), 0L));
            result.put("voted", votedTopicIds.contains(topic.getTopicNum()));
            return result;
        }).collect(Collectors.toList());
    }
}