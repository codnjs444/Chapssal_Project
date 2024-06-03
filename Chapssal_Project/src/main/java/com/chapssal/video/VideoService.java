package com.chapssal.video;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.chapssal.comment.CommentService;
import com.chapssal.hashtag.HashtagService;
import com.chapssal.user.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Service
public class VideoService {
    private final VideoRepository videoRepository;
    private final HashtagService hashtagService;
    private final VideoLikeService videoLikeService;
    private final CommentService commentService;

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
    
    public List<Video> findVideosByUsers(List<User> users) {
        return videoRepository.findByUserInOrderByVideoNumDesc(users);
    }

    public List<Video> findTopVideosByTopic(Integer topic) {
        return videoRepository.findTopVideosByTopic(topic);
    }

    public List<Object[]> findTopVideos() {
        return videoRepository.findTopVideos();
    }

    public List<Video> getTopVideosByTopic(Integer topic) {
        return videoRepository.findTopVideosByTopic(topic);
    }
    
    @Getter
    @Setter
    public static class VideoWithLikesAndComments {
        private Video video;
        private int likeCount;
        private int commentCount;

        public VideoWithLikesAndComments(Video video, int likeCount, int commentCount) {
            this.video = video;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
        }
    }

    public List<VideoWithLikesAndComments> getAllVideosWithLikeAndCommentCounts() {
        return videoRepository.findAll().stream()
                .map(video -> {
                    int likeCount = videoLikeService.countLikesByVideoId(video.getVideoNum());
                    int commentCount = commentService.countCommentsByVideoNum(video.getVideoNum());
                    return new VideoWithLikesAndComments(video, likeCount, commentCount);
                })
                .collect(Collectors.toList());
    }
    
    public List<VideoWithLikesAndComments> getVideosWithLikeAndCommentCounts(List<User> users) {
        return videoRepository.findByUserInOrderByVideoNumDesc(users).stream()
                .map(video -> {
                    int likeCount = videoLikeService.countLikesByVideoId(video.getVideoNum());
                    int commentCount = commentService.countCommentsByVideoNum(video.getVideoNum());
                    return new VideoWithLikesAndComments(video, likeCount, commentCount);
                })
                .collect(Collectors.toList());
    }
}
