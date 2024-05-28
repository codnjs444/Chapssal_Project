package com.chapssal.hashtag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HashtagController {
	
	private final HashtagService hashtagService;

    @Autowired
    public HashtagController(HashtagService hashtagService) {
        this.hashtagService = hashtagService;
    }

    @GetMapping("/hashtag/suggestions")
    public List<HashtagDTO> getHashtagSuggestions(@RequestParam(name = "query") String query) {
        return hashtagService.getHashtagSuggestions(query).stream()
                .map(hashtag -> new HashtagDTO(hashtag.getTag()))
                .collect(Collectors.toList());
    }

    static class HashtagDTO {
        private String tag;

        public HashtagDTO(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}
