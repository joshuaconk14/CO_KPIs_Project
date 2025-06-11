package com.instagram.kpi.service;

import com.instagram.kpi.model.InstagramPost;
import com.instagram.kpi.repository.InstagramPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InstagramService {
    private static final Logger log = LoggerFactory.getLogger(InstagramService.class);
    private final InstagramPostRepository postRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public InstagramService(InstagramPostRepository postRepository, SimpMessagingTemplate messagingTemplate) {
        this.postRepository = postRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<InstagramPost> getAllPosts() {
        return postRepository.findAll();
    }

    public InstagramPost getPostById(String postId) {
        return postRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
    }

    @Transactional
    public void fetchLatestPosts() {
        try {
            // TODO: Implement actual Instagram API integration here
            // For now, we'll just log that this method was called
            log.info("Fetching latest posts from Instagram API");
            
            // After fetching new data, send updates through WebSocket
            List<InstagramPost> updatedPosts = postRepository.findAll();
            messagingTemplate.convertAndSend("/topic/kpi-updates", updatedPosts);
            
            log.info("Successfully sent {} posts through WebSocket", updatedPosts.size());
        } catch (Exception e) {
            log.error("Error fetching latest posts", e);
            throw new RuntimeException("Failed to fetch latest posts", e);
        }
    }
} 