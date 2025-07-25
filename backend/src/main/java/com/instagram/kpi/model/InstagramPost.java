package com.instagram.kpi.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "instagram_posts")
public class InstagramPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String postId;

    @Column(length = 2000)
    private String caption;

    @Column(nullable = false)
    private LocalDateTime postedAt;

    private Integer likes;
    private Integer comments;
    private Integer shares;
    private Integer saves;
    private Integer reach;
    private Integer impressions;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Manual getters and setters
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }
    
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
    
    public Integer getReach() { return reach; }
    public void setReach(Integer reach) { this.reach = reach; }
    
    public Integer getImpressions() { return impressions; }
    public void setImpressions(Integer impressions) { this.impressions = impressions; }

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