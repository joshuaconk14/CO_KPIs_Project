package com.instagram.kpi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "instagram_story")
public class InstagramStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String storyId;

    @Column(nullable = false)
    private LocalDateTime postedAt;

    private Integer replies;
    private Integer shares;
    private Integer impressions;
    private Integer profileVisits;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Manual getters and setters
    public String getStoryId() { return storyId; }
    public void setStoryId(String storyId) { this.storyId = storyId; }
    
    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
    
    public Integer getReplies() { return replies; }
    public void setReplies(Integer replies) { this.replies = replies; }
    
    public Integer getShares() { return shares; }
    public void setShares(Integer shares) { this.shares = shares; }
    
    public Integer getImpressions() { return impressions; }
    public void setImpressions(Integer impressions) { this.impressions = impressions; }
    
    public Integer getProfileVisits() { return profileVisits; }
    public void setProfileVisits(Integer profileVisits) { this.profileVisits = profileVisits; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 