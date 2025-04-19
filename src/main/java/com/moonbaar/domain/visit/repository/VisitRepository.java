package com.moonbaar.domain.visit.repository;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.entity.Visit;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findTopByUserAndEventOrderByVisitedAtDesc(User user, CulturalEvent event);

    @Query("SELECT v FROM Visit v " +
            "JOIN FETCH v.event e " +
            "WHERE v.user.id = :userId " +
            "AND e.latitude >= :minLat " +
            "AND e.latitude <= :maxLat " +
            "AND e.longitude >= :minLng " +
            "AND e.longitude <= :maxLng ")
    List<Visit> findFootprintsByUserIdAndBounds(
            @Param("userId") Long userId,
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng);
}
