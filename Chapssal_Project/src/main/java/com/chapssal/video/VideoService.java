package com.chapssal.video;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    public int countVideosByUserNum(Integer userNum) {
        return videoRepository.countByUser_UserNum(userNum);
    }
    
    public List<Video> getVideosByUserNum(Integer userNum) {
        return videoRepository.findByUser_UserNumOrderByUploadDateDesc(userNum);
    }
    
    public int getPrevVideoId(int videoNum, int userNum) {
        Optional<Video> prevVideo = videoRepository.findFirstByUser_UserNumAndVideoNumLessThanOrderByVideoNumDesc(userNum, videoNum);
        return prevVideo.map(Video::getVideoNum).orElse(0);
    }

    public int getNextVideoId(int videoNum, int userNum) {
        Optional<Video> nextVideo = videoRepository.findFirstByUser_UserNumAndVideoNumGreaterThanOrderByVideoNumAsc(userNum, videoNum);
        return nextVideo.map(Video::getVideoNum).orElse(0);
    }
    
    public void delete(int videoNum) {
        videoRepository.deleteById(videoNum);
    }

    public List<Video> searchByTitle(String title) {
        return videoRepository.findByTitleContaining(title);
    }

    public List<String> findTitlesByQuery(String query) {
        return videoRepository.findByTitleContaining(query)
                .stream()
                .map(Video::getTitle)
                .collect(Collectors.toList());
    }

}
