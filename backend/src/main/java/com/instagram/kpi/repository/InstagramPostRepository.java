package com.instagram.kpi.repository;

import com.instagram.kpi.model.InstagramPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstagramPostRepository extends JpaRepository<InstagramPost, Long> {
    Optional<InstagramPost> findByPostId(String postId);
} 