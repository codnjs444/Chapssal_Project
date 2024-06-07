package com.chapssal.follow;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.chapssal.notification.NotificationService;
import com.chapssal.notification.NotificationType;
import com.chapssal.user.User;
import com.chapssal.user.UserRepository;

@RestController
@RequestMapping("/followHome")
public class FollowControllerHome {

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
        
        String message = followerUser.getUserName() + "님이 팔로우했습니다.";
        notificationService.createNotification(followingUser, NotificationType.FOLLOW, followerUser, message, null);

        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestParam(name = "follower") Integer follower, @RequestParam(name = "following") Integer following) {
        Follow follow = followRepository.findFirstByFollowerAndFollowing(follower, following);
        if (follow != null) {
            followRepository.delete(follow);
            return ResponseEntity.ok().body("{\"success\": true}");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"success\": false, \"message\": \"팔로우 관계를 찾을 수 없습니다.\"}");
        }
    }
}
