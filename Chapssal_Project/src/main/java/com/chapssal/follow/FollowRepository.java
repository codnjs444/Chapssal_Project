package com.chapssal.follow;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FollowRepository extends JpaRepository<Follow, Integer> {
    int countByFollowing(Integer following);
    int countByFollower(Integer follower);
    
    List<Follow> findByFollower(Integer follower);
    List<Follow> findByFollowing(Integer following);

    boolean existsByFollowerAndFollowing(Integer follower, Integer following);
    Follow findByFollowerAndFollowing(Integer follower, Integer following);

    Follow findFirstByFollowerAndFollowing(Integer follower, Integer following);
}

