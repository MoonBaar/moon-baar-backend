package com.moonbaar.domain.event.repository;

import com.moonbaar.domain.event.entity.CulturalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CulturalEventRepository extends JpaRepository<CulturalEvent, Long>,
        JpaSpecificationExecutor<CulturalEvent> {
}
