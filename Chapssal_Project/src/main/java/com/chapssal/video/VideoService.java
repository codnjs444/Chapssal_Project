package com.chapssal.video;

import java.time.LocalDateTime;

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
}
