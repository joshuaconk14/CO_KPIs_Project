package com.instagram.kpi.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.instagram.kpi.model.InstagramPost;
import com.instagram.kpi.repository.InstagramPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstagramService {
    private final InstagramPostRepository postRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${instagram.api.username}")
    private String username;

    @Value("${instagram.api.password}")
    private String password;

    private IGClient igClient;

    public void initialize() {
        try {
            igClient = IGClient.builder()
                    .username(username)
                    .password(password)
                    .login();
            log.info("Successfully connected to Instagram API");
        } catch (Exception e) {
            log.error("Failed to connect to Instagram API", e);
        }
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void fetchLatestPosts() {
        try {
            if (igClient == null) {
                initialize();
            }

            List<TimelineMedia> mediaList = igClient.getTimelineFeed()
                    .get()
                    .getFeed_items()
                    .stream()
                    .map(item -> item.getMedia_or_ad())
                    .collect(Collectors.toList());

            for (TimelineMedia media : mediaList) {
                InstagramPost post = new InstagramPost();
                post.setPostId(media.getId());
                post.setCaption(media.getCaption() != null ? media.getCaption().getText() : "");
                post.setPostedAt(LocalDateTime.ofInstant(media.getTaken_at(), java.time.ZoneId.systemDefault()));
                post.setLikes(media.getLike_count());
                post.setComments(media.getComment_count());
                post.setReach(media.getView_count());
                post.setImpressions(media.getPlay_count());

                InstagramPost savedPost = postRepository.save(post);
                
                // Send update through WebSocket
                messagingTemplate.convertAndSend("/topic/kpi-updates", savedPost);
            }
        } catch (Exception e) {
            log.error("Error fetching Instagram posts", e);
        }
    }

    public List<InstagramPost> getAllPosts() {
        return postRepository.findAll();
    }

    public InstagramPost getPostById(String postId) {
        return postRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }
} 