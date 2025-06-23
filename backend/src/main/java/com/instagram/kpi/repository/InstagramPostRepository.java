package com.instagram.kpi.repository;

import com.instagram.kpi.model.InstagramPost;
import org.springframework.data.jpa.repository.JpaRepository; // CRUDs
import org.springframework.stereotype.Repository;

import java.util.Optional;

// provides CRUD methods (-> findBy.Post.Id )so dont need to write SQL code
@Repository
public interface InstagramPostRepository extends JpaRepository<InstagramPost, Long> {
    Optional<InstagramPost> findByPostId(String postId);
} 