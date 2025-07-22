package com.instagram.kpi.controller;

import com.instagram.kpi.model.InstagramPost;
import com.instagram.kpi.model.InstagramStory;
import com.instagram.kpi.repository.InstagramStoryRepository;
import com.instagram.kpi.service.InstagramService;
// import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instagram")
public class InstagramController {
    private final InstagramService instagramService;
    private final InstagramStoryRepository storyRepository;

    public InstagramController(InstagramService instagramService, InstagramStoryRepository storyRepository) {
        this.instagramService = instagramService;
        this.storyRepository = storyRepository;
    }

    @GetMapping("/posts")
    public ResponseEntity<List<InstagramPost>> getAllPosts() {
        return ResponseEntity.ok(instagramService.getAllPosts());
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<InstagramPost> getPostById(@PathVariable String postId) {
        return ResponseEntity.ok(instagramService.getPostById(postId));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshPosts() {
        instagramService.refreshAllData();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/latest-story")
    public ResponseEntity<InstagramStory> getLatestStory() {
        return storyRepository.findTopByOrderByPostedAtDesc()
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
} 