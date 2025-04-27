package com.moonbaar.domain.badge.repository;

import com.moonbaar.domain.badge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    @Query("SELECT ub FROM UserBadge ub JOIN FETCH ub.badge WHERE ub.user.id = :userId")
    List<UserBadge> findByUser(Long userId);

    @Query("SELECT b.code FROM UserBadge ub JOIN ub.badge b WHERE ub.user.id = :userId")
    Set<String> findCodeByUser(Long userId);

}
