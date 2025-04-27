package com.moonbaar.domain.badge.controller;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.badge.dto.UserBadgeListResponse;
import com.moonbaar.domain.badge.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    /**
     * @param userDetails 로그인된 사용자
     * @return 사용자가 보유한 배지 목록
     */
    @GetMapping
    public UserBadgeListResponse findBadges(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return badgeService.findUserBadges(userDetails.getUser());
    }

}
