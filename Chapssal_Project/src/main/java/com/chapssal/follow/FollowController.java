package com.chapssal.follow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import com.chapssal.notification.NotificationService;
import com.chapssal.notification.NotificationType;
import com.chapssal.user.User;
import com.chapssal.user.UserRepository;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> followUser(@RequestBody FollowRequest followRequest) {
        Follow follow = new Follow();
        follow.setFollower(followRequest.getFollower());
        follow.setFollowing(followRequest.getFollowing());
        follow.setFollowDate(LocalDateTime.now());
        followRepository.save(follow);
        
        // 알림 생성
        User followerUser = userRepository.findById(followRequest.getFollower()).orElseThrow(() -> new IllegalArgumentException("Invalid follower ID"));
        User followingUser = userRepository.findById(followRequest.getFollowing()).orElseThrow(() -> new IllegalArgumentException("Invalid following ID"));
        
        notificationService.createNotification(followingUser, NotificationType.FOLLOW, followerUser, followerUser.getUserName() + "님이 팔로우했습니다.");

        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestBody FollowRequest followRequest) {
        Follow follow = followRepository.findByFollowerAndFollowing(followRequest.getFollower(), followRequest.getFollowing());
        if (follow != null) {
            followRepository.delete(follow);
            return ResponseEntity.ok().body("{\"success\": true}");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"success\": false, \"message\": \"팔로우 관계를 찾을 수 없습니다.\"}");
        }
    }
}
