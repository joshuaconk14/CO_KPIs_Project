package com.instagram.kpi.controller;

import com.instagram.kpi.model.InstagramPost;
import com.instagram.kpi.service.InstagramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instagram")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InstagramController {
    private final InstagramService instagramService;

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
        instagramService.fetchLatestPosts();
        return ResponseEntity.ok().build();
    }
} 