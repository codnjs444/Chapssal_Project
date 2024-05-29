package com.chapssal.video;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chapssal.hashtag.HashtagService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final HashtagService hashtagService;

    public void create(Video video) {
        video.setUploadDate(LocalDateTime.now());
        this.videoRepository.save(video);
        hashtagService.extractAndSaveHashtags(video.getTitle(), video); // 해시태그 추출 및 저장 로직 추가
    }

    public Optional<Video> findById(int videoNum) {
        return videoRepository.findById(videoNum);
    }

    public List<Video> findAll() {
        return videoRepository.findAll();
    }
    
    public int countVideosByUserNum(Integer userNum) {
        return videoRepository.countByUser_UserNum(userNum);
    }
}
