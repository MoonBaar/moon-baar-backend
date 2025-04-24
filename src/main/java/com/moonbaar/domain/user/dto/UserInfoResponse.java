package com.moonbaar.domain.user.dto;

import com.moonbaar.domain.user.entity.User;

import java.time.LocalDateTime;

public record UserInfoResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
