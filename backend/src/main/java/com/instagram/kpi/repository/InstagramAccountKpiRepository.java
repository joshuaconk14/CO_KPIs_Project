package com.instagram.kpi.repository;

import com.instagram.kpi.model.InstagramAccountKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface InstagramAccountKpiRepository extends JpaRepository<InstagramAccountKpi, Long> {
} 