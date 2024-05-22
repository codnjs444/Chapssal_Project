package com.chapssal.follow;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface FollowRepository extends JpaRepository<Follow, Integer> {
    int countByFollowing(Integer following);
    int countByFollower(Integer follower);
    
    List<Follow> findByFollower(Integer follower);
    List<Follow> findByFollowing(Integer following);

}

