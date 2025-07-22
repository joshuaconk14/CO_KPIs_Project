package com.instagram.kpi.service;

import com.instagram.kpi.model.InstagramPost;
import com.instagram.kpi.model.InstagramAccountKpi;
import com.instagram.kpi.repository.InstagramPostRepository;
import com.instagram.kpi.repository.InstagramAccountKpiRepository;
import com.instagram.kpi.repository.InstagramStoryRepository;
import com.instagram.kpi.model.InstagramStory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.*;
import java.util.Properties;

@Service
public class InstagramService {
    private static final Logger log = LoggerFactory.getLogger(InstagramService.class);

    private static final DateTimeFormatter INSTAGRAM_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private final InstagramPostRepository postRepository;
    private final InstagramAccountKpiRepository accountKpiRepository;
    private final InstagramStoryRepository storyRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebClient webClient;

    @Value("${instagram.api.graph-api-version}")
    private String graphApiVersion;

    @Value("${instagram.api.access-token}")
    private String accessToken;

    @Value("${instagram.api.business-account-id}")
    private String businessAccountId;

    @Value("${facebook.app-id}")
    private String facebookAppId;

    @Value("${facebook.app-secret}")
    private String facebookAppSecret;

    @Value("${facebook.short-lived-token}")
    private String facebookShortLivedToken;

    private static final String ENV_FILE_PATH = "backend/.env";

    private static final long FIFTY_DAYS_MILLIS = 50L * 24 * 60 * 60 * 1000;

    public InstagramService(InstagramPostRepository postRepository,
                            InstagramAccountKpiRepository accountKpiRepository,
                            InstagramStoryRepository storyRepository,
                            SimpMessagingTemplate messagingTemplate,
                            WebClient.Builder webClientBuilder) {
        this.postRepository = postRepository;
        this.accountKpiRepository = accountKpiRepository;
        this.storyRepository = storyRepository;
        this.messagingTemplate = messagingTemplate;
        this.webClient = webClientBuilder.baseUrl("https://graph.facebook.com").build();
    }

    public List<InstagramPost> getAllPosts() {
        return postRepository.findAll();
    }

    public InstagramPost getPostById(String postId) {
        return postRepository.findByPostId(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
    }

    @Scheduled(fixedRate = 3600000) // Run every hour (3,600,000 milliseconds)
    public void scheduledFetch() {
        log.info("--- Starting scheduled data fetch ---");
        refreshAllData();
        log.info("--- Finished scheduled data fetch ---");
    }

    public void refreshAllData() {
        log.info("--- Refreshing all Instagram data ---");
        fetchLatestPosts();
        fetchAccountKpis();
        fetchPinnedReel();
        fetchLatestStory();
    }

    @Transactional
    public void fetchLatestPosts() {
        try {
            log.info("Fetching latest posts from Instagram API for account: {}", businessAccountId);
            
            // Enhanced field list for better data coverage
            String mediaUrl = String.format("/%s/%s/media?fields=id,caption,media_type,timestamp,permalink,like_count,comments_count,insights.metric(reach,impressions,saved,comments,shares,likes)&access_token=%s",
                graphApiVersion, businessAccountId, accessToken);

            JsonNode response = webClient.get()
                .uri(mediaUrl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

            List<InstagramPost> fetchedPosts = new ArrayList<>();
            if (response != null && response.has("data")) {
                for (JsonNode postNode : response.get("data")) {
                    String postId = postNode.get("id").asText();
                    
                    Optional<InstagramPost> existingPostOpt = postRepository.findByPostId(postId);
                    InstagramPost post = existingPostOpt.orElse(new InstagramPost());
                    post.setPostId(postId);

                    if (postNode.has("caption")) {
                        post.setCaption(postNode.get("caption").asText());
                    }
                    if (postNode.has("timestamp")) {
                        String timestampStr = postNode.get("timestamp").asText();
                        post.setPostedAt(ZonedDateTime.parse(timestampStr, INSTAGRAM_DATE_FORMATTER).toLocalDateTime());
                    }
                    if (postNode.has("like_count")) {
                        post.setLikes(postNode.get("like_count").asInt());
                    }
                     if (postNode.has("comments_count")) {
                        post.setComments(postNode.get("comments_count").asInt());
                    }
                    
                    // Parse insights if available in the response
                    if (postNode.has("insights") && postNode.get("insights").has("data")) {
                        for (JsonNode insightNode : postNode.get("insights").get("data")) {
                            String name = insightNode.get("name").asText();
                            int value = insightNode.get("values").get(0).get("value").asInt();
                            switch (name) {
                                case "reach":
                                    post.setReach(value);
                                    break;
                                case "impressions":
                                    post.setImpressions(value);
                                    break;
                                case "saved":
                                    post.setSaves(value);
                                    break;
                            }
                        }
                    } else {
                        // Fallback to separate insights call if not included in main response
                    setPostInsights(post);
                    }

                    fetchedPosts.add(postRepository.save(post));
                }
            }
            
            messagingTemplate.convertAndSend("/topic/kpi-updates", fetchedPosts);
            log.info("Successfully fetched and updated {} posts.", fetchedPosts.size());
        } catch (Exception e) {
            log.error("Error fetching latest posts", e);
            throw new RuntimeException("Failed to fetch latest posts", e);
        }
    }

    @Transactional
    public void fetchPinnedReel() {
        try {
            log.info("Fetching pinned reel from Instagram API for account: {}", businessAccountId);
            
            // Remove media_type filter and get all media, then filter for reels
            String pinnedReelUrl = String.format("/%s/%s/media?fields=id,caption,media_type,timestamp,permalink,like_count,comments_count,insights.metric(comments,shares,likes,saves,avg_watch_time)&limit=50&access_token=%s",
                graphApiVersion, businessAccountId, accessToken);

            JsonNode response = webClient.get()
                .uri(pinnedReelUrl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

            if (response != null && response.has("data")) {
                // Find the first reel in the response
                for (JsonNode mediaNode : response.get("data")) {
                    if (mediaNode.has("media_type") && "REELS".equals(mediaNode.get("media_type").asText())) {
                        log.info("Successfully fetched pinned reel data: {}", mediaNode.get("id").asText());
                        // TODO: Save to InstagramPinnedReel model
                        break; // Get the first reel only
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching pinned reel", e);
        }
    }

    @Transactional
    public void fetchLatestStory() {
        try {
            log.info("Fetching latest story from Instagram API for account: {}", businessAccountId);
            
            String latestStoryUrl = String.format("/%s/%s/media?fields=id,media_type,timestamp,insights.metric(replies,shares,impressions,profile_visits)&media_type=STORY&limit=1&access_token=%s",
                graphApiVersion, businessAccountId, accessToken);

            JsonNode response = webClient.get()
                .uri(latestStoryUrl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

            if (response != null && response.has("data") && response.get("data").size() > 0) {
                JsonNode storyNode = response.get("data").get(0);
                InstagramStory story = new InstagramStory();
                story.setStoryId(storyNode.get("id").asText());
                if (storyNode.has("timestamp")) {
                    story.setPostedAt(ZonedDateTime.parse(storyNode.get("timestamp").asText(), INSTAGRAM_DATE_FORMATTER).toLocalDateTime());
                }
                if (storyNode.has("insights") && storyNode.get("insights").has("data")) {
                    for (JsonNode insightNode : storyNode.get("insights").get("data")) {
                        String name = insightNode.get("name").asText();
                        int value = insightNode.get("values").get(0).get("value").asInt();
                        switch (name) {
                            case "replies":
                                story.setReplies(value);
                                break;
                            case "shares":
                                story.setShares(value);
                                break;
                            case "impressions":
                                story.setImpressions(value);
                                break;
                            case "profile_visits":
                                story.setProfileVisits(value);
                                break;
                        }
                    }
                }
                storyRepository.save(story);
                log.info("Successfully fetched and saved latest story data: {}", story.getStoryId());
            }
        } catch (Exception e) {
            log.error("Error fetching latest story", e);
        }
    }

    @Transactional
    public void fetchAccountKpis() {
        log.info("Fetching account level KPIs");
        try {
            LocalDate today = LocalDate.now();
            LocalDate thirtyDaysAgo = today.minusDays(30);

            // 1. Fetch daily reach for the last 30 days
            final String reachUrl = String.format(
                "/%s/%s/insights?metric=reach&period=day&since=%s&until=%s&access_token=%s",
                graphApiVersion, businessAccountId, thirtyDaysAgo, today, accessToken);

            JsonNode reachResponse = webClient.get().uri(reachUrl).retrieve().bodyToMono(JsonNode.class).block();

            if (reachResponse != null && reachResponse.has("data") && reachResponse.get("data").get(0).has("values")) {
                for (JsonNode dailyReach : reachResponse.get("data").get(0).get("values")) {
                    LocalDate date = ZonedDateTime.parse(dailyReach.get("end_time").asText(), INSTAGRAM_DATE_FORMATTER).toLocalDate();
                    int reachValue = dailyReach.get("value").asInt();

                    InstagramAccountKpi kpi = accountKpiRepository.findByDate(date)
                        .orElse(new InstagramAccountKpi());
                    
                    kpi.setDate(date);
                    kpi.setReach(reachValue);
                    accountKpiRepository.save(kpi);
                }
                log.info("Successfully fetched and updated 30-day reach data.");
            }

            // 2. Fetch today's total followers and profile views and add to today's record
            InstagramAccountKpi todayKpi = accountKpiRepository.findByDate(today).orElse(new InstagramAccountKpi());
            todayKpi.setDate(today);

            final String followersUrl = String.format(
                "/%s/%s?fields=followers_count&access_token=%s",
                graphApiVersion, businessAccountId, accessToken);
            
            JsonNode followersResponse = webClient.get().uri(followersUrl).retrieve().bodyToMono(JsonNode.class).block();
            if (followersResponse != null && followersResponse.has("followers_count")) {
                todayKpi.setFollowers(followersResponse.get("followers_count").asInt());
            }

            final String profileViewsUrl = String.format(
                "/%s/%s/insights?metric=profile_views&period=day&metric_type=total_value&access_token=%s",
                graphApiVersion, businessAccountId, accessToken);
                
            JsonNode profileViewsResponse = webClient.get().uri(profileViewsUrl).retrieve().bodyToMono(JsonNode.class).block();
            if (profileViewsResponse != null && profileViewsResponse.has("data") && profileViewsResponse.get("data").size() > 0) {
                JsonNode profileViewsData = profileViewsResponse.get("data").get(0);
                if(profileViewsData.has("total_value") && profileViewsData.get("total_value").has("value")) {
                    todayKpi.setProfileViews(profileViewsData.get("total_value").get("value").asInt());
                }
            }
            
            // 3. Calculate New Followers for today
            accountKpiRepository.findByDate(today.minusDays(1)).ifPresent(yesterdayKpi -> {
                if (todayKpi.getFollowers() != null && yesterdayKpi.getFollowers() != null) {
                    todayKpi.setNewFollowers(todayKpi.getFollowers() - yesterdayKpi.getFollowers());
                }
            });
            if (todayKpi.getNewFollowers() == null) {
                todayKpi.setNewFollowers(0);
            }

            accountKpiRepository.save(todayKpi);
            log.info("Successfully fetched and updated today's KPIs: {}", todayKpi);

            // 4. Send all data for the last 30 days to the frontend
            messagingTemplate.convertAndSend("/topic/kpi-updates", accountKpiRepository.findAll());

        } catch(Exception e) {
            log.error("Could not fetch account KPIs", e);
        }
    }

    @Scheduled(fixedRate = FIFTY_DAYS_MILLIS) // 50 days in milliseconds
    public void refreshLongLivedToken() {
        log.info("Attempting to refresh long-lived Instagram token...");
        try {
            String url = String.format(
                "https://graph.facebook.com/v23.0/oauth/access_token?grant_type=fb_exchange_token&client_id=%s&client_secret=%s&fb_exchange_token=%s",
                facebookAppId, facebookAppSecret, facebookShortLivedToken
            );
            String response = WebClient.create().get().uri(url).retrieve().bodyToMono(String.class).block();
            if (response != null && response.contains("access_token")) {
                String newToken = response.split("\"access_token\":\"")[1].split("\"")[0];
                updateEnvFile("INSTAGRAM_USER_ACCESS_TOKEN", newToken);
                log.info("Successfully refreshed and updated long-lived token in .env file.");
            } else {
                log.error("Failed to parse new token from response: {}", response);
            }
        } catch (Exception e) {
            log.error("Error refreshing long-lived token", e);
        }
    }

    private void updateEnvFile(String key, String value) throws IOException {
        File envFile = new File(ENV_FILE_PATH);
        Properties props = new Properties();
        if (envFile.exists()) {
            try (FileInputStream fis = new FileInputStream(envFile)) {
                props.load(fis);
            }
        }
        props.setProperty(key, value);
        try (FileOutputStream fos = new FileOutputStream(envFile)) {
            props.store(fos, null);
        }
    }

    private void setPostInsights(InstagramPost post) {
        try {
            final String insightsUrl = String.format(
                "/%s/%s/insights?metric=reach,impressions,saved&access_token=%s",
                graphApiVersion, post.getPostId(), accessToken);
            
            JsonNode insightsResponse = webClient.get()
                .uri(insightsUrl)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

            if (insightsResponse != null && insightsResponse.has("data")) {
                for (JsonNode insightNode : insightsResponse.get("data")) {
                    String name = insightNode.get("name").asText();
                    int value = insightNode.get("values").get(0).get("value").asInt();
                    switch (name) {
                        case "reach":
                            post.setReach(value);
                            break;
                        case "impressions":
                            post.setImpressions(value);
                            break;
                        case "saved":
                            post.setSaves(value);
                            break;
                    }
                }
            }
        } catch(Exception e) {
            log.error("Could not fetch insights for post {}: {}", post.getPostId(), e.getMessage());
        }
    }
}