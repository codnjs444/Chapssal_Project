package com.chapssal.ranking;

import com.chapssal.award.Award;
import com.chapssal.award.AwardRepository;
import com.chapssal.school.School;
import com.chapssal.school.SchoolRepository;
import com.chapssal.util.MonthUtil;
import com.chapssal.util.NumberUtil;
import com.chapssal.video.Video;
import com.chapssal.video.VideoLike;
import com.chapssal.video.VideoLikeRepository;
import com.chapssal.video.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Service
public class RankingService {

    @Autowired
    private AwardRepository awardRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoLikeRepository videoLikeRepository;

    // 검색 결과를 기준으로 필터링
    public Map<String, SchoolRanking> getSchoolRankings(LocalDate date) {
        return calculateRankings(date, null);
    }

    public Map<String, SchoolRanking> searchSchoolRankings(LocalDate date, String search) {
        return calculateRankings(date, search);
    }

    // 특정 월의 학교 랭킹을 조회하는 메서드
    private Map<String, SchoolRanking> calculateRankings(LocalDate date, String search) {
        // 여기서 date는 이미 현재 달의 마지막 날을 가리키고 있음
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());

        // Award 테이블에서 해당 월에 포함된 모든 수상내역 조회
        List<Award> awards = awardRepository.findByAwardDateBetween(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        // 수상 내역에 해당하는 유저를 찾고 해당 유저들의 학교 랭킹을 세움
        Map<String, List<Award>> awardsBySchool = awards.stream()
                .collect(Collectors.groupingBy(award -> award.getUser().getSchool().getSchoolName()));

        Map<String, SchoolRanking> schoolRankings = new LinkedHashMap<>();

        for (Map.Entry<String, List<Award>> entry : awardsBySchool.entrySet()) {
            String schoolName = entry.getKey();
            List<Award> schoolAwards = entry.getValue();

            int goldCount = (int) schoolAwards.stream().filter(a -> a.getAwardType() == 3).count();
            int silverCount = (int) schoolAwards.stream().filter(a -> a.getAwardType() == 2).count();
            int bronzeCount = (int) schoolAwards.stream().filter(a -> a.getAwardType() == 1).count();

            // AwardType을 통해 점수를 매김
            int score = goldCount * 3 + silverCount * 2 + bronzeCount;

            School school = schoolRepository.findBySchoolName(schoolName);
            SchoolRanking schoolRanking = new SchoolRanking(schoolName, score, school.getSchoolPictureUrl(), goldCount, silverCount, bronzeCount);

            // Video 관련 집계
            List<Video> videos = videoRepository.findByUser_School_SchoolNameAndUploadDateBetween(
                    schoolName, startDate.atStartOfDay(), endDate.atTime(23, 59, 59)
            );

            int uploadedVideoCount = videos.size();
            int totalViews = videos.stream().mapToInt(Video::getViewCount).sum();

            // VideoLike 관련 집계
            List<VideoLike> videoLikes = videoLikeRepository.findByVideo_User_School_SchoolNameAndLikeDateBetween(
                    schoolName, startDate.atStartOfDay(), endDate.atTime(23, 59, 59)
            );

            int totalLikes = videoLikes.size();

            schoolRanking.setUploadedVideoCount(uploadedVideoCount);
            schoolRanking.setTotalViewsFormatted(NumberUtil.formatNumber(totalViews));
            schoolRanking.setTotalLikes(totalLikes);

            schoolRankings.put(schoolName, schoolRanking);
        }

        // 학교별 랭킹 정보를 SchoolRanking 객체로 변환하여 반환
        List<SchoolRanking> sortedRankings = schoolRankings.values().stream()
                .sorted((sr1, sr2) -> sr2.getScore() - sr1.getScore())
                .collect(Collectors.toList());

        for (int i = 0; i < sortedRankings.size(); i++) {
            sortedRankings.get(i).setRank(i + 1);
        }

        Map<String, SchoolRanking> finalRankings = sortedRankings.stream().collect(
                Collectors.toMap(
                        SchoolRanking::getSchoolName,
                        sr -> sr,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                )
        );

        if (search != null) {
            finalRankings = finalRankings.entrySet().stream()
                    .filter(entry -> entry.getKey().contains(search))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
        }

        return finalRankings;
    }
}
