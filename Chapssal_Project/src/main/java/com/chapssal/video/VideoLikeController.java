package com.chapssal.video;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chapssal.user.User;
import com.chapssal.user.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoLikeController {

    @Autowired
    private final VideoService videoService;
    @Autowired
    private final UserService userService;
    @Autowired
    private final VideoLikeService videoLikeService;

    @PostMapping("/like")
    public ResponseEntity<?> likeVideo(@RequestBody VideoLikeRequest likeRequest) {
        Video video = videoService.findById(likeRequest.getVideo()).orElseThrow(() -> new IllegalArgumentException("Invalid video ID"));
        User user = userService.findById(likeRequest.getUser()).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        videoLikeService.likeVideo(video, user);

        int likeCount = videoLikeService.countLikesByVideoId(video.getVideoNum());
        return ResponseEntity.ok().body("{\"success\": true, \"likeCount\": " + likeCount + "}");
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<?> unlikeVideo(@RequestBody VideoLikeRequest videoLikeRequest) {
        List<VideoLike> videoLikes = videoLikeService.findByVideoAndUser(videoLikeRequest.getVideo(), videoLikeRequest.getUser());
        if (!videoLikes.isEmpty()) {
            videoLikeService.deleteByVideoAndUser(videoLikeRequest.getVideo(), videoLikeRequest.getUser());
            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"좋아요를 취소하였습니다.\"}");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"success\": false, \"message\": \"Like relationship not found.\"}");
        }
    }
}
