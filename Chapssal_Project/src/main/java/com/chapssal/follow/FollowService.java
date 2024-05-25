package com.chapssal.follow;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chapssal.notification.NotificationService;
import com.chapssal.user.User;
import com.chapssal.user.UserRepository;
import com.chapssal.notification.NotificationType;

@Service
public class FollowService {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;

    public int countFollowingByUserNum(Integer userNum) {
        return followRepository.countByFollowing(userNum);
    }
    
    public int countFollowerByUserNum(Integer userNum) {
        return followRepository.countByFollower(userNum);
    }
    
    public List<User> getFollowingUsers(Integer userNum) {
        List<Follow> followings = followRepository.findByFollower(userNum);
        return followings.stream()
                         .map(follow -> userRepository.findById(follow.getFollowing()).orElse(null))
                         .filter(Objects::nonNull) // null 값 필터링
                         .collect(Collectors.toList());
    }

    public List<User> getFollowerUsers(Integer userNum) {
        List<Follow> followers = followRepository.findByFollowing(userNum);
        return followers.stream()
                        .map(follow -> userRepository.findById(follow.getFollower()).orElse(null))
                        .filter(Objects::nonNull) // null 값 필터링
                        .collect(Collectors.toList());
    }

    public boolean isFollowing(Integer follower, Integer following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
    
    public void followUser(Integer follower, Integer following) {
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setFollowDate(LocalDateTime.now());

        followRepository.save(follow);
        
        // 팔로우 알림 생성
        User followerUser = userRepository.findById(follower).orElseThrow(() -> new IllegalArgumentException("Invalid follower ID"));
        User followingUser = userRepository.findById(following).orElseThrow(() -> new IllegalArgumentException("Invalid following ID"));
        
        notificationService.createNotification(followingUser, NotificationType.FOLLOW, followerUser, followerUser.getUserName() + "님이 팔로우했습니다.");
    }
}
