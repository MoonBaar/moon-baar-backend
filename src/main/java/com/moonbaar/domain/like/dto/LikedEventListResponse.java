package com.moonbaar.domain.like.dto;

import com.moonbaar.domain.like.entity.LikedEvent;
import java.util.List;
import org.springframework.data.domain.Page;

public record LikedEventListResponse(
        Long totalCount,
        int totalPages,
        int currentPage,
        List<LikedEventResponse> events
) {

    public static LikedEventListResponse from(Page<LikedEvent> likedEvents) {
        return new LikedEventListResponse(
          likedEvents.getTotalElements(),
          likedEvents.getTotalPages(),
          likedEvents.getNumber() + 1,
          LikedEventResponse.fromList(likedEvents.getContent())
        );
    }
}
