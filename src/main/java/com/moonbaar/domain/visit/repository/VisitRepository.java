package com.moonbaar.domain.visit.repository;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.entity.Visit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findTopByUserAndEventOrderByVisitedAtDesc(User user, CulturalEvent event);
}
