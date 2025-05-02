package com.moonbaar.domain.event.dto;

import com.moonbaar.domain.category.entity.Category;
import com.moonbaar.domain.district.entity.District;
import com.moonbaar.domain.event.entity.CulturalEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public record EventDetailResponse(
        Long id,
        String title,
        String category,
        String district,
        String place,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean isFree,
        String useFee,
        String useTarget,
        String player,
        String program,
        String etcDesc,
        String mainImg,
        String orgName,
        String orgLink,
        String hmpgAddr,
        BigDecimal latitude,
        BigDecimal longitude,
        long visitCount,
        long likeCount
) {

    public static EventDetailResponse of(CulturalEvent event, long visitCount, long likeCount) {
        String categoryName = Optional.ofNullable(event.getCategory())
                .map(Category::getName)
                .orElse(null);

        String districtName = Optional.ofNullable(event.getDistrict())
                .map(District::getName)
                .orElse(null);

        boolean isFreeEvent = "무료".equals(event.getIsFree());

        return new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                categoryName,
                districtName,
                event.getPlace(),
                event.getStartDate(),
                event.getEndDate(),
                isFreeEvent,
                event.getUseFee(),
                event.getUseTarget(),
                event.getPlayer(),
                event.getProgram(),
                event.getEtcDesc(),
                event.getMainImg(),
                event.getOrgName(),
                event.getOrgLink(),
                event.getHmpgAddr(),
                event.getLatitude(),
                event.getLongitude(),
                visitCount,
                likeCount
        );
    }
}
