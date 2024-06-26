package com.chapssal.video;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;

import com.chapssal.comment.CommentService;
import com.chapssal.hashtag.HashtagService;
import com.chapssal.user.User;
import com.chapssal.user.UserService;

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
    private final UserService userService;
    private final VideoLikeRepository videoLikeRepository;

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
    
    @Transactional
    public void incrementViewCount(int videoNum) {
        Video video = videoRepository.findById(videoNum).orElseThrow(() -> new IllegalArgumentException("Invalid video ID"));
        video.setViewCount(video.getViewCount() + 1);
        videoRepository.save(video);
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
    
    public List<VideoWithLikesAndComments> getTopVideosByLikesInLastHour() {
        return videoRepository.findTopVideosByLikesInLastHour().stream()
                .map(result -> {
                    int videoNum = (Integer) result[0];
                    String title = (String) result[1];
                    String videoUrl = (String) result[2];
                    String thumbnailUrl = (String) result[3];
                    int userNum = (Integer) result[4];
                    Integer topic = (Integer) result[5];
                    int likeCount = ((Number) result[5]).intValue();

                    Video video = new Video();
                    video.setVideoNum(videoNum);
                    video.setTitle(title);
                    video.setVideoUrl(videoUrl);
                    video.setThumbnailUrl(thumbnailUrl);
                    video.setTopic(topic);
                    // User 객체는 필요한 경우 UserService를 통해 가져와야 합니다.
                    User user = userService.findByUserNum(userNum);
                    video.setUser(user);

                    int commentCount = commentService.countCommentsByVideoNum(video.getVideoNum());
                    return new VideoWithLikesAndComments(video, likeCount, commentCount);
                })
                .collect(Collectors.toList());
    }

    public List<VideoWithLikesAndComments> getAllVideosOrderedByLikes() {
        return videoRepository.findAllVideosOrderedByLikes().stream()
                .map(result -> {
                    int videoNum = ((Number) result[0]).intValue();
                    String title = (String) result[1];
                    String videoUrl = (String) result[2];
                    String thumbnailUrl = (String) result[3];
                    int userNum = ((Number) result[4]).intValue();
                    Integer topic = (Integer) result[5];
                    int likeCount = ((Number) result[6]).intValue();
                    int recentLikeCount = ((Number) result[7]).intValue();
                    int viewCount = ((Number) result[8]).intValue(); // viewCount 추가

                    Video video = new Video();
                    video.setVideoNum(videoNum);
                    video.setTitle(title);
                    video.setVideoUrl(videoUrl);
                    video.setThumbnailUrl(thumbnailUrl);
                    video.setTopic(topic);
                    video.setViewCount(viewCount); // viewCount 설정
                    // User 객체는 필요한 경우 UserService를 통해 가져와야 합니다.
                    User user = userService.findByUserNum(userNum);
                    video.setUser(user);

                    int commentCount = commentService.countCommentsByVideoNum(video.getVideoNum());
                    return new VideoWithLikesAndComments(video, likeCount, commentCount);
                })
                .collect(Collectors.toList());
    }
    
    public List<Object[]> findTopVideosForWeek(LocalDateTime startDate, LocalDateTime endDate) {
        return videoRepository.findTopVideosForWeek(startDate, endDate);
    }

    public List<Object[]> findTopVideosByLikesInUploadWeek(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> likesInWeek = videoLikeRepository.findLikeCountForVideosInWeek(startDate, endDate);
        return likesInWeek.stream()
                .filter(videoLike -> {
                    Video video = videoRepository.findById((Integer) videoLike[0]).orElse(null);
                    if (video == null) {
                        return false;
                    }
                    LocalDate videoUploadDate = video.getUploadDate().toLocalDate();
                    LocalDate startOfUploadWeek = videoUploadDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    LocalDate endOfUploadWeek = startOfUploadWeek.plusDays(6);
                    LocalDateTime uploadStartOfWeek = startOfUploadWeek.atStartOfDay();
                    LocalDateTime uploadEndOfWeek = endOfUploadWeek.atTime(LocalTime.MAX);
                    return !(startDate.isAfter(uploadEndOfWeek) || endDate.isBefore(uploadStartOfWeek));
                })
                .collect(Collectors.toList());
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
    
    public List<VideoWithLikesAndComments> getAllVideosOrderedByUploadDate() {
        return videoRepository.findAllVideosOrderByUploadDateDesc().stream()
                .map(video -> {
                    int likeCount = videoLikeService.countLikesByVideoId(video.getVideoNum());
                    int commentCount = commentService.countCommentsByVideoNum(video.getVideoNum());
                    return new VideoWithLikesAndComments(video, likeCount, commentCount);
                })
                .collect(Collectors.toList());
    }
    public int getNextVideoIdByTopic(int videoNum, int topicNum) {
        List<Video> videos = videoRepository.findByTopicOrderByVideoNumAsc(topicNum);
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getVideoNum() == videoNum && i < videos.size() - 1) {
                return videos.get(i + 1).getVideoNum();
            }
        }
        return 0; // 다음 영상이 없을 경우 0 반환
    }

    public int getPrevVideoIdByTopic(int videoNum, int topicNum) {
        List<Video> videos = videoRepository.findByTopicOrderByVideoNumAsc(topicNum);
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getVideoNum() == videoNum && i > 0) {
                return videos.get(i - 1).getVideoNum();
            }
        }
        return 0; // 이전 영상이 없을 경우 0 반환
    }

    public List<VideoWithLikesAndComments> getAllVideosOrderedByLikesAndTopic(int topic) {
        return videoRepository.findAllVideosOrderedByLikesAndTopic(topic).stream()
            .map(result -> {
                int videoNum = (Integer) result[0];
                String title = (String) result[1];
                String videoUrl = (String) result[2];
                String thumbnailUrl = (String) result[3];
                int userNum = (Integer) result[4];
                Integer videoTopic = (Integer) result[5];
                int likeCount = ((Number) result[6]).intValue();
                int recentLikeCount = ((Number) result[7]).intValue();
                int viewCount = ((Number) result[8]).intValue();

                Video video = new Video();
                video.setVideoNum(videoNum);
                video.setTitle(title);
                video.setVideoUrl(videoUrl);
                video.setThumbnailUrl(thumbnailUrl);
                video.setTopic(videoTopic);
                video.setViewCount(viewCount);
                User user = userService.findByUserNum(userNum);
                video.setUser(user);

                int commentCount = commentService.countCommentsByVideoNum(video.getVideoNum());
                return new VideoWithLikesAndComments(video, likeCount, commentCount);
            })
            .collect(Collectors.toList());
    }
    

    public List<Object[]> findTopVideosForWeekAndTopic(LocalDateTime startDate, LocalDateTime endDate, int topic) {
        return videoRepository.findTopVideosForWeekAndTopic(startDate, endDate, topic);
    }

    // 페이지네이션 메소드 추가
    public Page<VideoWithLikesAndComments> getAllVideosPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videoPage = videoRepository.findAllVideos(pageable);
        return videoPage.map(video -> {
            int likeCount = videoLikeService.countLikesByVideoId(video.getVideoNum());
            int commentCount = commentService.countCommentsByVideoNum(video.getVideoNum());
            return new VideoWithLikesAndComments(video, likeCount, commentCount);
        });
    }

    public Page<VideoWithLikesAndComments> getVideosByTopicPaginated(int topic, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> videoPage = videoRepository.findVideosByTopic(topic, pageable);
        return videoPage.map(video -> {
            int likeCount = videoLikeService.countLikesByVideoId(video.getVideoNum());
            int commentCount = commentService.countCommentsByVideoNum(video.getVideoNum());
            return new VideoWithLikesAndComments(video, likeCount, commentCount);
        });
    }
    
    public List<Video> getUserVideos(int userNum) {
        return videoRepository.findByUserUserNum(userNum);
    }
    public List<Video> getLikedVideosByUser(int userNum) {
        return videoLikeRepository.findLikedVideosByUser(userNum);
    }
}
 