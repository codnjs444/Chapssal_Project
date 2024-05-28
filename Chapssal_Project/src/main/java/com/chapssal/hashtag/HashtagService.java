package com.chapssal.hashtag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    @Autowired
    public HashtagService(HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    @Transactional
    public void extractAndSaveHashtags(String title) {
        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(title);
        while (matcher.find()) {
            String tag = matcher.group(1);
            Hashtag hashtag = hashtagRepository.findByTag(tag)
                    .orElseGet(() -> new Hashtag());
            hashtag.setTag(tag);
            hashtag.setHashtagCount(hashtag.getHashtagCount() != null ? hashtag.getHashtagCount() + 1 : 1);
            hashtagRepository.save(hashtag);
        }
    }

    public List<Hashtag> getHashtagSuggestions(String query) {
        if (query.isEmpty()) {
            return hashtagRepository.findTop5ByOrderByHashtagCountDesc();
        } else {
            return hashtagRepository.findByTagStartingWith(query);
        }
    }
}
