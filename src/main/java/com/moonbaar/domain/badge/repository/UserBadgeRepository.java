package com.moonbaar.domain.badge.repository;

import com.moonbaar.domain.badge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    @Query("SELECT b.code FROM UserBadge ub JOIN ub.badge b WHERE ub.user.id = :userId")
    Set<String> findCodeByUserId(Long userId);

}
