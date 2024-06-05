package com.chapssal.message.controller;

import com.chapssal.user.User;
import com.chapssal.user.UserService;
import com.chapssal.video.HashtagService;
import com.chapssal.video.Video;
import com.chapssal.video.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        if (query.startsWith("@")) {
            String userName = query.substring(1);
            List<User> users = userService.searchByUserName(userName);
            model.addAttribute("results", users);
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
            List<Video> videoResults = videoService.searchByTitle(query);
            List<Video> hashtagResults = hashtagService.searchVideosByHashtag(query);
            model.addAttribute("userResults", userResults);
            model.addAttribute("videoResults", videoResults);
            model.addAttribute("hashtagResults", hashtagResults);
            model.addAttribute("searchType", "general");
            logger.info("General search user results: {}", userResults);
            logger.info("General search video results: {}", videoResults);
            logger.info("General search hashtag results: {}", hashtagResults);
        }
        return "message/search";
    }
}
