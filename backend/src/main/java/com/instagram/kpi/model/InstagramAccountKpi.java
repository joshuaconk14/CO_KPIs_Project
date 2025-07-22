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

    // Manual getters and setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public Integer getFollowers() { return followers; }
    public void setFollowers(Integer followers) { this.followers = followers; }
    
    public Integer getNewFollowers() { return newFollowers; }
    public void setNewFollowers(Integer newFollowers) { this.newFollowers = newFollowers; }
    
    public Integer getProfileViews() { return profileViews; }
    public void setProfileViews(Integer profileViews) { this.profileViews = profileViews; }
    
    public Integer getReach() { return reach; }
    public void setReach(Integer reach) { this.reach = reach; }
    
    public Integer getPinnedReelComments() { return pinnedReelComments; }
    public void setPinnedReelComments(Integer pinnedReelComments) { this.pinnedReelComments = pinnedReelComments; }
    
    public Integer getPinnedReelShares() { return pinnedReelShares; }
    public void setPinnedReelShares(Integer pinnedReelShares) { this.pinnedReelShares = pinnedReelShares; }
    
    public Integer getPinnedReelLikes() { return pinnedReelLikes; }
    public void setPinnedReelLikes(Integer pinnedReelLikes) { this.pinnedReelLikes = pinnedReelLikes; }
    
    public Integer getPinnedReelSaves() { return pinnedReelSaves; }
    public void setPinnedReelSaves(Integer pinnedReelSaves) { this.pinnedReelSaves = pinnedReelSaves; }
    
    public Integer getPinnedReelWatchTime() { return pinnedReelWatchTime; }
    public void setPinnedReelWatchTime(Integer pinnedReelWatchTime) { this.pinnedReelWatchTime = pinnedReelWatchTime; }
} 