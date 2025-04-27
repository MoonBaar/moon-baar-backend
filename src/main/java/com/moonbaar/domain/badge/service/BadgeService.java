package com.moonbaar.domain.badge.service;

import com.moonbaar.domain.badge.dto.*;
import com.moonbaar.domain.badge.entity.UserBadge;
import com.moonbaar.domain.badge.repository.UserBadgeRepository;
import com.moonbaar.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final UserBadgeRepository userBadgeRepository;

    public UserBadgeListResponse findUserBadges(User user) {
        List<UserBadge> userBadges = userBadgeRepository.findByUser(user.getId());
        return UserBadgeListResponse.from(userBadges);
    }

}
