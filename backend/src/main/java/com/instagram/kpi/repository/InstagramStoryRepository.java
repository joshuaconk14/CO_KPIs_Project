package com.instagram.kpi.repository;

import com.instagram.kpi.model.InstagramStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstagramStoryRepository extends JpaRepository<InstagramStory, Long> {
    Optional<InstagramStory> findTopByOrderByPostedAtDesc();
} 