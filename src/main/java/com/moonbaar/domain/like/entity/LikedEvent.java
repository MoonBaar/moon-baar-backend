package com.moonbaar.domain.like.entity;

import com.moonbaar.common.entity.BaseEntity;
import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "liked_events",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "event_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LikedEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_liked_event_user_id",
                    foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
            )
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_liked_event_event_id",
                    foreignKeyDefinition = "FOREIGN KEY (event_id) REFERENCES cultural_events(id) ON DELETE CASCADE"
            ))
    private CulturalEvent event;
}
