package com.moonbaar.domain.district.repository;

import com.moonbaar.domain.district.entity.District;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

    @Query("SELECT d FROM District d WHERE d.name = :name")
    Optional<District> findByName(String name);
}
