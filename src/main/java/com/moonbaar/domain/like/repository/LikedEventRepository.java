package com.moonbaar.domain.like.repository;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.like.entity.LikedEvent;
import com.moonbaar.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedEventRepository extends JpaRepository<LikedEvent, Long> {

  boolean existsByUserAndEvent(User user, CulturalEvent event);
}
