package com.moonbaar.domain.badge.entity;

import com.moonbaar.common.entity.BaseEntityWithUpdate;
import com.moonbaar.domain.badge.enums.BadgeCode;
import com.moonbaar.domain.badge.enums.BadgeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Badge extends BaseEntityWithUpdate {

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private BadgeType badgeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, unique = true)
    private BadgeCode code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

}