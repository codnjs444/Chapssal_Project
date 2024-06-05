package com.chapssal.hashtag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chapssal.video.Video;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final VideoHashtagRepository videoHashtagRepository;

    public HashtagService(HashtagRepository hashtagRepository, VideoHashtagRepository videoHashtagRepository) {
        this.hashtagRepository = hashtagRepository;
        this.videoHashtagRepository = videoHashtagRepository;
    }

    @Transactional
    public void extractAndSaveHashtags(String title, Video video) {
        Pattern pattern = Pattern.compile("#([\\w가-힣]+)"); // 정규식 수정: \\w가 아닌 [\\w가-힣]로 변경
        Matcher matcher = pattern.matcher(title);
        while (matcher.find()) {
            String tag = matcher.group(1);
            Hashtag hashtag = hashtagRepository.findByTag(tag)
                    .orElseGet(() -> {
                        Hashtag newHashtag = new Hashtag();
                        newHashtag.setTag(tag);
                        newHashtag.setHashtagCount(0);
                        return newHashtag;
                    });

            // Save the hashtag first if it's new
            if (hashtag.getHashtagNum() == 0) {
                hashtagRepository.save(hashtag);
            }

            // VideoHashtag 중복 저장 방지
            if (!videoHashtagRepository.existsByVideoAndHashtag(video, hashtag)) {
                VideoHashtag videoHashtag = new VideoHashtag();
                videoHashtag.setVideo(video);
                videoHashtag.setHashtag(hashtag);
                videoHashtagRepository.save(videoHashtag);

                // 해시태그 카운트 증가 및 저장 (중복 저장 방지)
                hashtag.setHashtagCount(hashtag.getHashtagCount() + 1);
                hashtagRepository.save(hashtag);
            }
        }
    }

    public List<Hashtag> getHashtagSuggestions(String query) {
        if (query.isEmpty()) {
            return hashtagRepository.findTop5ByOrderByHashtagCountDesc();
        } else {
            return hashtagRepository.findByTagContaining(query); // 수정된 부분
        }
    }

    public List<Video> searchVideosByHashtag(String hashtag) {
        return videoHashtagRepository.findVideosByHashtagTag(hashtag);
    }

}
