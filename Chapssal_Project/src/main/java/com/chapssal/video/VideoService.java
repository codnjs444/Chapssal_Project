package com.chapssal.video;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VideoService {
    private final VideoRepository videoRepository;

    public void create(Video video) {
        video.setUploadDate(LocalDateTime.now());
        this.videoRepository.save(video);
    }

    public Optional<Video> findById(int videoNum) {
        return videoRepository.findById(videoNum);
    }

    public List<Video> findAll() {
        return videoRepository.findAll();
    }
}
