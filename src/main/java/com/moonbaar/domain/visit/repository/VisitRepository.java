package com.moonbaar.domain.visit.repository;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.statistics.dto.VisitCountByName;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.entity.Visit;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findTopByUserAndEventOrderByVisitedAtDesc(User user, CulturalEvent event);

    @Query("SELECT v FROM Visit v JOIN FETCH v.event WHERE v.user = :user AND v.visitedAt >= :startDateTime")
    Page<Visit> findByUserAndVisitedAtAfter(@Param("user") User user,
                                            @Param("startDateTime") LocalDateTime startDateTime,
                                            Pageable pageable);

    boolean existsByUserAndEvent(User user, CulturalEvent event);

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

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(v) FROM Visit v WHERE v.user.id = :userId AND v.visitedAt >= :startOfMonth")
    long countByUserIdAndVisitedAtAfter(
            @Param("userId") Long userId,
            @Param("startOfMonth") LocalDateTime startOfMonth);
    
    @Query("SELECT new com.moonbaar.domain.statistics.dto.VisitCountByName(c.name, COUNT(v)) " +
            "FROM Visit v JOIN v.event e JOIN e.category c " +
            "WHERE v.user.id = :userId " +
            "GROUP BY c.name " +
            "ORDER BY COUNT(v) DESC")
    List<VisitCountByName> countVisitsByCategory(@Param("userId") Long userId);

    @Query("SELECT new com.moonbaar.domain.statistics.dto.VisitCountByName(d.name, COUNT(v)) " +
            "FROM Visit v JOIN v.event e JOIN e.district d " +
            "WHERE v.user.id = :userId " +
            "GROUP BY d.name " +
            "ORDER BY COUNT(v) DESC")
    List<VisitCountByName> countVisitsByDistrict(@Param("userId") Long userId);
}
