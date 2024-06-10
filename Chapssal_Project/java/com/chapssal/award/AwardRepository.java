package com.chapssal.award;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwardRepository extends JpaRepository<Award, Integer> {
    List<Award> findByUser_UserNum(int userNum);
    List<Award> findByAwardDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
