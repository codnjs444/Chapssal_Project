package com.chapssal.ranking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolRanking {
    private String schoolName;
    private int score;
    private String schoolPictureUrl;
    private int goldCount;
    private int silverCount;
    private int bronzeCount;
    private int rank;
    private int uploadedVideoCount;
    private String totalViewsFormatted;
    private int totalLikes;



    public SchoolRanking(String schoolName, int score, String schoolPictureUrl,
                         int goldCount, int silverCount, int bronzeCount) {
        this.schoolName = schoolName;
        this.score = score;
        this.schoolPictureUrl = schoolPictureUrl;
        this.goldCount = goldCount;
        this.silverCount = silverCount;
        this.bronzeCount = bronzeCount;

    }
}