package com.chapssal.follow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowRepository followRepository;

    @PostMapping
    public ResponseEntity<?> followUser(@RequestBody FollowRequest followRequest) {
        Follow follow = new Follow();
        follow.setFollower(followRequest.getFollower());
        follow.setFollowing(followRequest.getFollowing());
        follow.setFollowDate(LocalDateTime.now());
        followRepository.save(follow);

        return ResponseEntity.ok().body("{\"success\": true}");
    }
}
