package com.chapssal.message.controller;

import com.chapssal.hashtag.HashtagService;
import com.chapssal.message.model.SearchQuery;
import com.chapssal.message.repository.SearchQueryRepository;
import com.chapssal.topic.SelectedTopicService;
import com.chapssal.user.User;
import com.chapssal.user.UserService;
import com.chapssal.video.Video;
import com.chapssal.video.VideoService;
import com.chapssal.follow.FollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/message")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private SearchQueryRepository searchQueryRepository;

    @Autowired
    private FollowService followService;

    @Autowired
    private SelectedTopicService selectedTopicService;

    @ModelAttribute("topicsByVoteCount")
    public List<Object[]> topicsByVoteCount() {
//        return selectedTopicService.findTopicsByVoteCount();
    	return selectedTopicService.getTopicsByVoteCountForLastWeek();
    }
    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        // 검색어 저장
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(query);
        searchQuery.setSearchDate(new Date());
        searchQueryRepository.save(searchQuery);

        if (query.startsWith("@")) {
            String username = query.substring(1);
            List<User> users = userService.searchByUserName(username);
            List<Map<String, Object>> usersWithFollowerCounts = users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("user", user);
                userMap.put("followerCount", followService.countFollowingByUserNum(user.getUserNum()));
                return userMap;
            }).collect(Collectors.toList());
            model.addAttribute("results", usersWithFollowerCounts);
            model.addAttribute("searchType", "user");
            logger.info("User search results: {}", users);
        } else if (query.startsWith("#")) {
            String hashtag = query.substring(1);
            List<Video> videos = hashtagService.searchVideosByHashtag(hashtag);
            model.addAttribute("results", videos);
            model.addAttribute("searchType", "hashtag");
            logger.info("Hashtag search results: {}", videos);
        } else {
            List<User> userResults = userService.searchByUserName(query);
            List<Map<String, Object>> usersWithFollowerCounts = userResults.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("user", user);
                userMap.put("followerCount", followService.countFollowingByUserNum(user.getUserNum()));
                return userMap;
            }).collect(Collectors.toList());
            List<Video> videoResults = videoService.searchByTitle(query);
            List<Video> hashtagResults = hashtagService.searchVideosByHashtag(query);
            model.addAttribute("userResults", usersWithFollowerCounts);
            model.addAttribute("videoResults", videoResults);
            model.addAttribute("hashtagResults", hashtagResults);
            model.addAttribute("searchType", "general");
            logger.info("General search user results: {}", userResults);
            logger.info("General search video results: {}", videoResults);
            logger.info("General search hashtag results: {}", hashtagResults);
        }
        return "message/search";
    }

    @GetMapping("/search/suggestions")
    @ResponseBody
    public List<String> getSuggestions(@RequestParam("query") String query) {
        List<String> videoTitles = videoService.findTitlesByQuery(query);
        List<String> userNames = userService.findUserNamesByQuery(query);

        List<Object[]> topSearchQueries = searchQueryRepository.findTopSearchQueries();
        List<String> topSearchSuggestions = topSearchQueries.stream()
                .map(result -> (String) result[0])
                .collect(Collectors.toList());

        return Stream.concat(Stream.concat(videoTitles.stream(), userNames.stream()), topSearchSuggestions.stream())
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    @GetMapping("/search/popular")
    @ResponseBody
    public List<String> getPopularSearches() {
        // Get the current time and subtract 1 hour to find popular searches from the last hour
        Date oneHourAgo = new Date(System.currentTimeMillis() - 3600 * 1000);

        List<Object[]> topSearchQueries = searchQueryRepository.findTopSearchQueriesSince(oneHourAgo);
        return topSearchQueries.stream()
                .map(result -> (String) result[0])
                .collect(Collectors.toList());
    }
}
