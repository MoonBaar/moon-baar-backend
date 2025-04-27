package com.moonbaar.domain.badge.service;

import com.moonbaar.domain.badge.dto.*;
import com.moonbaar.domain.badge.entity.Badge;
import com.moonbaar.domain.badge.entity.UserBadge;
import com.moonbaar.domain.badge.enums.BadgeCode;
import com.moonbaar.domain.badge.exception.BadgeNotFoundException;
import com.moonbaar.domain.badge.repository.BadgeRepository;
import com.moonbaar.domain.badge.repository.UserBadgeRepository;
import com.moonbaar.domain.statistics.dto.VisitCountByName;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final VisitRepository visitRepository;

    public UserBadgeListResponse findUserBadges(User user) {
        List<UserBadge> userBadges = userBadgeRepository.findByUser(user.getId());
        return UserBadgeListResponse.from(userBadges);
    }

    /**
     * 사용자가 새롭게 얻은 배지를 저장하고 응답한다.
     * @param user 로그인된 사용자
     * @return 새롭게 얻은 배지 목록
     */
    @Transactional
    public UserBadgeListResponse checkAndGrantUserBadges(User user) {
        // 배지 획득 여부 판단에 필요한 데이터 계산
        BadgeEvaluationContext badgeEvaluationContext = createBadgeEvaluationContext(user.getId());

        // 기존에 소유한 배지 코드 목록
        Set<String> ownedBadgeCodes = userBadgeRepository.findCodeByUser(user.getId());

        // 새롭게 얻은 배지 목록
        List<UserBadgeResponse> newlyGrantedBadges = new ArrayList<>();

        for (BadgeCode badgeCode : BadgeCode.values()) {
            if (ownedBadgeCodes.contains(badgeCode.name())) {
                continue; // 이미 소유한 배지는 건너뜀
            }

            // 조건을 충족한 배지 저장
            if (badgeCode.isAchieved(badgeEvaluationContext)) {
                Badge badge = badgeRepository.getByCode(badgeCode).orElseThrow(BadgeNotFoundException::new);
                UserBadge userBadge = UserBadge.builder()
                        .user(user)
                        .badge(badge)
                        .build();
                userBadgeRepository.save(userBadge);

                newlyGrantedBadges.add(UserBadgeResponse.from(userBadge));
            }
        }
        return new UserBadgeListResponse(newlyGrantedBadges);
    }

    /**
     * 사용자의 활동 기록을 조회하여 배지 획득 여부 판단에 필요한 데이터를 계산한다.
     * @param userId 로그인된 사용자 아이디
     * @return BadgeEvaluationContext 계산된 사용자 활동 데이터
     */
    private BadgeEvaluationContext createBadgeEvaluationContext(Long userId) {
        long totalVisitCount = visitRepository.countByUserId(userId);

        List<VisitCountByName> visitCountByDistrictNames = visitRepository.countVisitsByDistrict(userId);
        int visitedDistrictCount = visitCountByDistrictNames.size();

        List<VisitCountByName> visitCountByCategoryNames = visitRepository.countVisitsByCategory(userId);
        Map<String, Long> visitCountByCategory = visitCountByCategoryNames.stream()
                .collect(Collectors.toMap(
                        VisitCountByName::name,
                        VisitCountByName::count
                ));

        return new BadgeEvaluationContext(totalVisitCount, visitedDistrictCount, visitCountByCategory);
    }

    /**
     * 진행률이 가장 높은 배지를 조회한다.
     * @param user 로그인된 사용자
     * @return 진행률이 가장 높은 배지 정보 및 진행 정보
     */
    public Optional<BadgeProgressResponse> findNextTargetBadge(User user) {
        BadgeEvaluationContext badgeEvaluationContext = createBadgeEvaluationContext(user.getId());
        Set<String> ownedBadgeCodes = userBadgeRepository.findCodeByUser(user.getId());

        BadgeProgressResponse badgeProgressResponse = null;
        double maximumProgressRate = 0.0;

        for (BadgeCode badgeCode : BadgeCode.values()) {
            if (ownedBadgeCodes.contains(badgeCode.name())) {
                continue; // 이미 소유한 배지는 건너뜀
            }

            long progress = badgeCode.getProgress(badgeEvaluationContext);
            long target = badgeCode.getTarget();
            if (target <= 0) {
                continue;
            }
            double progressRate = ((double) progress / target) * 100.0;
            if (progressRate >= 100.0) {
                continue;
            }

            if (progressRate > maximumProgressRate) {
                maximumProgressRate = progressRate;

                Badge badge = badgeRepository.getByCode(badgeCode).orElseThrow(BadgeNotFoundException::new);

                badgeProgressResponse = new BadgeProgressResponse(
                        badge.getId(),
                        badge.getCode().name(),
                        badge.getName(),
                        badge.getDescription(),
                        progress,
                        target
                );
            }
        }
        return Optional.ofNullable(badgeProgressResponse);
    }

}
