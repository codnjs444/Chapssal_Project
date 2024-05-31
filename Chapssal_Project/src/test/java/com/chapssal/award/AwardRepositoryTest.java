package com.chapssal.award;

import com.chapssal.user.User;
import com.chapssal.user.UserRepository;
import com.chapssal.video.Video;
import com.chapssal.video.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class AwardRepositoryTest {

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    public void testRandomAwardCreation() {
        Random random = new Random();

        // 해당 유저들 중 랜덤으로 id 삽입
        int[] userIds = {32, 62, 49, 61, 45, 26, 30, 43, 52, 1};
        List<Video> videos = videoRepository.findAll();

        for (int i = 0; i < 30; i++) {
            Award award = new Award();

            // 임의의 유저 설정
            User user = userRepository.findById(userIds[random.nextInt(userIds.length)]).orElseThrow(() -> new RuntimeException("User not found"));
            award.setUser(user);

            // 임의의 비디오 설정
            Video video = videos.get(random.nextInt(videos.size()));
            award.setVideo(video);

            award.setAwardName("Award " + (i + 1));
            award.setAwardType(random.nextInt(3) + 1);
            award.setAwardDate(LocalDateTime.of(2024, random.nextInt(3) + 3, random.nextInt(28) + 1, 12, 0));
            awardRepository.save(award);
        }

        // Validate that awards are saved correctly
        long count = awardRepository.count();
        assert(count >= 30); // 최소 30개의 수상 내역이 있어야 함
    }
}
