package com.moonbaar.domain.badge.repository;

import com.moonbaar.domain.badge.entity.Badge;
import com.moonbaar.domain.badge.enums.BadgeCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> getByCode(BadgeCode code);

}
