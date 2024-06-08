package com.chapssal.award;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwardService {

    @Autowired
    private AwardRepository awardRepository;

    public List<Award> getAwardsByUserNum(int userNum) {
        return awardRepository.findByUser_UserNum(userNum);
    }
}
