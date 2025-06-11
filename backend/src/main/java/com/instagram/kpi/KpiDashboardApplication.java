package com.instagram.kpi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KpiDashboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(KpiDashboardApplication.class, args);
    }
} 