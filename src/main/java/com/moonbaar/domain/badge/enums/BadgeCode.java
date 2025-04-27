package com.moonbaar.domain.badge.enums;

import com.moonbaar.domain.badge.dto.BadgeEvaluationContext;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum BadgeCode {

    FIRST_VISIT(stats -> stats.totalVisitCount(), 1),

    SEOUL_EXPLORER(stats -> (long) stats.visitedDistrictCount(), 10),

    SEOUL_MASTER(stats -> (long) stats.visitedDistrictCount(), 25),

    CULTURE_CLASS_EXPLORER(stats -> stats.getVisitCountByCategoryName("교육/체험"), 1),

    CULTURE_CLASS_MANIA(stats -> stats.getVisitCountByCategoryName("교육/체험"), 10),

    KOREAN_TRADITION_EXPLORER(stats -> stats.getVisitCountByCategoryName("국악"), 1),

    KOREAN_TRADITION_MANIA(stats -> stats.getVisitCountByCategoryName("국악"), 10),

    SOLO_PERFORMANCE_EXPLORER(stats -> stats.getVisitCountByCategoryName("독주/독창회"), 1),

    SOLO_PERFORMANCE_MANIA(stats -> stats.getVisitCountByCategoryName("독주/독창회"), 10),

    MUSICAL_OPERA_EXPLORER(stats -> stats.getVisitCountByCategoryName("뮤지컬/오페라"), 1),

    MUSICAL_OPERA_MANIA(stats -> stats.getVisitCountByCategoryName("뮤지컬/오페라"), 10),

    DANCE_EXPLORER(stats -> stats.getVisitCountByCategoryName("무용"), 1),

    DANCE_MANIA(stats -> stats.getVisitCountByCategoryName("무용"), 10),

    PLAY_EXPLORER(stats -> stats.getVisitCountByCategoryName("연극"), 1),

    PLAY_MANIA(stats -> stats.getVisitCountByCategoryName("연극"), 10),

    MOVIE_EXPLORER(stats -> stats.getVisitCountByCategoryName("영화"), 1),

    MOVIE_MANIA(stats -> stats.getVisitCountByCategoryName("영화"), 10),

    EXHIBITION_ART_EXPLORER(stats -> stats.getVisitCountByCategoryName("전시/미술"), 1),

    EXHIBITION_ART_MANIA(stats -> stats.getVisitCountByCategoryName("전시/미술"), 10),

    FESTIVAL_EXPLORER(stats -> stats.getVisitCountByCategoryPrefix("축제"), 1),

    FESTIVAL_MANIA(stats -> stats.getVisitCountByCategoryPrefix("축제"), 10),

    CLASSIC_EXPLORER(stats -> stats.getVisitCountByCategoryName("클래식"), 1),

    CLASSIC_MANIA(stats -> stats.getVisitCountByCategoryName("클래식"), 10),

    CONCERT_EXPLORER(stats -> stats.getVisitCountByCategoryName("콘서트"), 1),

    CONCERT_MANIA(stats -> stats.getVisitCountByCategoryName("콘서트"), 10),

    ETC_EXPLORER(stats -> stats.getVisitCountByCategoryName("기타"), 1),

    ETC_MANIA(stats -> stats.getVisitCountByCategoryName("기타"), 10);

    private final Function<BadgeEvaluationContext, Long> progressExtractor;
    private final long target;

    BadgeCode(Function<BadgeEvaluationContext, Long> progressExtractor, long target) {
        this.progressExtractor = progressExtractor;
        this.target = target;
    }

    public long getProgress(BadgeEvaluationContext context) {
        return progressExtractor.apply(context);
    }

    public boolean isAchieved(BadgeEvaluationContext context) {
        return getProgress(context) >= target;
    }

}
