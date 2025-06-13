package com.instagram.kpi.controller;

import com.instagram.kpi.model.InstagramAccountKpi;
import com.instagram.kpi.repository.InstagramAccountKpiRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instagram/account-kpis")
public class InstagramAccountKpiController {
    private final InstagramAccountKpiRepository kpiRepository;

    public InstagramAccountKpiController(InstagramAccountKpiRepository kpiRepository) {
        this.kpiRepository = kpiRepository;
    }

    @GetMapping
    public ResponseEntity<List<InstagramAccountKpi>> getAllAccountKpis() {
        return ResponseEntity.ok(kpiRepository.findAll());
    }
} 