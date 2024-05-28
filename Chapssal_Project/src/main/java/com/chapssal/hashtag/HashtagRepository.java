package com.chapssal.hashtag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Integer> {
    Optional<Hashtag> findByTag(String tag);

    List<Hashtag> findByTagStartingWith(String query);   

    @Query("SELECT h FROM Hashtag h ORDER BY h.hashtagCount DESC LIMIT 5")
    List<Hashtag> findTop5ByOrderByHashtagCountDesc();
}
