package com.moonbaar.domain.badge.controller;

import com.moonbaar.common.oauth.CustomUserDetails;
import com.moonbaar.domain.badge.dto.BadgeListResponse;
import com.moonbaar.domain.badge.dto.BadgeProgressResponse;
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
     * 전체 배지 목록과 사용자의 소유 여부를 조회한다.
     * @param userDetails 로그인된 사용자
     * @return 사용자가 보유한 배지 목록
     */
    @GetMapping
    public BadgeListResponse findBadges(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return badgeService.findUserBadges(userDetails.getUser());
    }

    /**
     * 사용자가 새로운 배지를 얻을 수 있는지 확인하고, 새로 얻은 배지 목록을 조회한다.
     * @param userDetails 로그인된 사용자
     * @return 새로 얻은 배지 목록
     */
    @PostMapping("/new")
    public UserBadgeListResponse checkAndGrantBadges(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return badgeService.checkAndGrantUserBadges(userDetails.getUser());
    }

    /**
     * 진행률이 가장 높은 배지를 조회한다.
     * @param userDetails 로그인된 사용자
     * @return 진행률이 가장 높은 배지 정보 및 진행 정보. 없으면 null
     */
    @GetMapping("/next")
    public BadgeProgressResponse findNextBadge(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return badgeService.findNextTargetBadge(userDetails.getUser())
                .orElse(null);
    }

}
