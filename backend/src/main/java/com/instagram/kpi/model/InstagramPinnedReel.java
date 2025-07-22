package com.instagram.kpi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "instagram_pinned_reel")
public class InstagramPinnedReel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reelId;

    @Column(length = 2000)
    private String caption;

    @Column(nullable = false)
    private LocalDateTime postedAt;

    private Integer likes;
    private Integer comments;
    private Integer shares;
    private Integer saves;
    private Integer avgWatchTime; // in seconds

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Manual getters and setters
    public String getReelId() { return reelId; }
    public void setReelId(String reelId) { this.reelId = reelId; }
    
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    
    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }
    
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    
    public Integer getComments() { return comments; }
    public void setComments(Integer comments) { this.comments = comments; }
    
    public Integer getShares() { return shares; }
    public void setShares(Integer shares) { this.shares = shares; }
    
    public Integer getSaves() { return saves; }
    public void setSaves(Integer saves) { this.saves = saves; }
    
    public Integer getAvgWatchTime() { return avgWatchTime; }
    public void setAvgWatchTime(Integer avgWatchTime) { this.avgWatchTime = avgWatchTime; }

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