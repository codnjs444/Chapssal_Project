package com.chapssal.video;

import com.chapssal.user.User;
import com.chapssal.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Random;

@SpringBootTest
public class VideoLikeRepositoryTest {

    @Autowired
    private VideoLikeRepository videoLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    public void testRandomVideoLikeCreation() {
        Random random = new Random();

        // 해당 유저들 중 랜덤으로 id 삽입
        int[] userIds = {34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48};
        int[] videoIds = {4, 5, 6, 7};

        for (int i = 0; i < 50; i++) {
            VideoLike videoLike = new VideoLike();

            // 임의의 유저 설정
            User user = userRepository.findById(userIds[random.nextInt(userIds.length)]).orElseThrow(() -> new RuntimeException("User not found"));
            videoLike.setUser(user);

            // 임의의 비디오 설정
            Video video = videoRepository.findById(videoIds[random.nextInt(videoIds.length)]).orElseThrow(() -> new RuntimeException("Video not found"));
            videoLike.setVideo(video);

            // 임의의 날짜 설정 (4월 1일 ~ 5월 31일)
            int month = random.nextInt(2) + 4; // 4 또는 5
            int day = random.nextInt(month == 4 ? 30 : 31) + 1;
            videoLike.setLikeDate(LocalDateTime.of(2024, month, day, random.nextInt(24), random.nextInt(60)));

            videoLikeRepository.save(videoLike);
        }

        // Validate that video likes are saved correctly
        long count = videoLikeRepository.count();
        assert(count >= 50); // 최소 50개의 좋아요 내역이 있어야 함
    }
}
