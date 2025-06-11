package com.instagram.kpi.service;

import com.instagram.kpi.model.InstagramPost;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TestDataService {
    private final Random random = new Random();
    private final List<InstagramPost> testPosts = new ArrayList<>();

    public TestDataService() {
        initializeTestData();
    }

    private void initializeTestData() {
        String[] captions = {
            "Beautiful sunset at the beach! 🌅 #nature #sunset",
            "New product launch! Check out our latest collection 🛍️ #fashion #new",
            "Team building day with amazing colleagues! 👥 #work #team",
            "Delicious food at the new restaurant downtown 🍽️ #foodie #dinner",
            "Morning workout routine 💪 #fitness #health"
        };

        for (int i = 0; i < 5; i++) {
            InstagramPost post = new InstagramPost();
            post.setPostId("TEST_" + (i + 1));
            post.setCaption(captions[i]);
            post.setPostedAt(LocalDateTime.now().minusDays(i));
            post.setLikes(random.nextInt(1000) + 100);
            post.setComments(random.nextInt(100) + 10);
            post.setShares(random.nextInt(50) + 5);
            post.setSaves(random.nextInt(200) + 20);
            post.setReach(random.nextInt(5000) + 1000);
            post.setImpressions(random.nextInt(8000) + 2000);
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            testPosts.add(post);
        }
    }

    public List<InstagramPost> getAllTestPosts() {
        return new ArrayList<>(testPosts);
    }

    public InstagramPost getTestPostById(String postId) {
        return testPosts.stream()
                .filter(post -> post.getPostId().equals(postId))
                .findFirst()
                .orElse(null);
    }

    public void updateTestData() {
        for (InstagramPost post : testPosts) {
            // Simulate engagement growth
            post.setLikes(post.getLikes() + random.nextInt(50));
            post.setComments(post.getComments() + random.nextInt(10));
            post.setShares(post.getShares() + random.nextInt(5));
            post.setSaves(post.getSaves() + random.nextInt(20));
            post.setReach(post.getReach() + random.nextInt(200));
            post.setImpressions(post.getImpressions() + random.nextInt(300));
            post.setUpdatedAt(LocalDateTime.now());
        }
    }
} 