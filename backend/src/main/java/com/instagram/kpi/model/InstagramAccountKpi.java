package com.instagram.kpi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import lombok.NoArgsConstructor; // constructor for class initialization

@Data
@NoArgsConstructor
@Entity
@Table(name = "instagram_account_kpi")
public class InstagramAccountKpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; // for time series (e.g., reach, profile views)
    private Integer followers;
    private Integer newFollowers;
    private Integer profileViews;
    private Integer reach;
    private Integer pinnedReelComments;
    private Integer pinnedReelShares;
    private Integer pinnedReelLikes;
    private Integer pinnedReelSaves;
    private Integer pinnedReelWatchTime;
} 