package com.chapssal.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/video")
public class VideoLikeController {

    @Autowired
    private VideoLikeRepository videoLikeRepository;

    @PostMapping("/like")
    public ResponseEntity<?> likeVideo(@RequestBody VideoLikeRequest videoLikeRequest) {
        VideoLike videoLike = new VideoLike();
        videoLike.setVideo(videoLikeRequest.getVideo());
        videoLike.setUser(videoLikeRequest.getUser());
        videoLike.setLikeDate(LocalDateTime.now());
        videoLikeRepository.save(videoLike);

        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<?> unlikeVideo(@RequestBody VideoLikeRequest videoLikeRequest) {
        VideoLike videoLike = videoLikeRepository.findByVideoAndUser(videoLikeRequest.getVideo(), videoLikeRequest.getUser());
        if (videoLike != null) {
            videoLikeRepository.delete(videoLike);
            return ResponseEntity.ok().body("{\"success\": true}");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"success\": false, \"message\": \"Like relationship not found.\"}");
        }
    }
}
