package com.chapssal.ranking;

import com.chapssal.award.Award;
import com.chapssal.award.AwardRepository;
import com.chapssal.school.School;
import com.chapssal.school.SchoolRepository;
import com.chapssal.util.MonthUtil;
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

    // 특정 월의 학교 랭킹을 조회하는 메서드
    public Map<String, SchoolRanking> getSchoolRankings(LocalDate date) {
        String formattedMonth = MonthUtil.getPreviousMonthFormatted(date);

        LocalDate startDate = LocalDate.parse(formattedMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

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

            schoolRankings.put(schoolName, schoolRanking);
        }

        // 학교별 랭킹 정보를 SchoolRanking 객체로 변환하여 반환
        return schoolRankings.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().getScore() - e1.getValue().getScore())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
