package com.instagram.kpi.repository;

import com.instagram.kpi.model.InstagramAccountKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
 
@Repository
public interface InstagramAccountKpiRepository extends JpaRepository<InstagramAccountKpi, Long> {
    Optional<InstagramAccountKpi> findTopByOrderByDateDesc();
    Optional<InstagramAccountKpi> findByDate(LocalDate date);
} 