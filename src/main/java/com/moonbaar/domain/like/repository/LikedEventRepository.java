package com.moonbaar.domain.like.repository;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.like.entity.LikedEvent;
import com.moonbaar.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikedEventRepository extends JpaRepository<LikedEvent, Long> {

  boolean existsByUserAndEvent(User user, CulturalEvent event);

  Optional<LikedEvent> findByUserAndEvent(User user, CulturalEvent event);

  @Query("SELECT l FROM LikedEvent l JOIN FETCH l.event WHERE l.user = :user")
  Page<LikedEvent> findByUser(User user, Pageable pageable);

  @Query("SELECT COUNT(l) FROM LikedEvent l WHERE l.event.id = :eventId")
  long countByEventId(@Param("eventId") Long eventId);
}
