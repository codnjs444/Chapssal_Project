package com.chapssal.follow;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chapssal.user.User;
import com.chapssal.user.UserRepository;

@Service
public class FollowService {
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;

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

    
}
