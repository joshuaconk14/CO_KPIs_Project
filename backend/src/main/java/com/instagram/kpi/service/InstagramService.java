package com.instagram.kpi.service;

import com.instagram.kpi.model.InstagramPost;
import com.instagram.kpi.model.InstagramAccountKpi;
import com.instagram.kpi.repository.InstagramPostRepository;
import com.instagram.kpi.repository.InstagramAccountKpiRepository;
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

@Service
public class InstagramService {
    private static final Logger log = LoggerFactory.getLogger(InstagramService.class);

    private static final DateTimeFormatter INSTAGRAM_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private final InstagramPostRepository postRepository;
    private final InstagramAccountKpiRepository accountKpiRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebClient webClient;

    @Value("${instagram.api.graph-api-version}")
    private String graphApiVersion;

    @Value("${instagram.api.access-token}")
    private String accessToken;

    @Value("${instagram.api.business-account-id}")
    private String businessAccountId;

    public InstagramService(InstagramPostRepository postRepository,
                            InstagramAccountKpiRepository accountKpiRepository,
                            SimpMessagingTemplate messagingTemplate,
                            WebClient.Builder webClientBuilder) {
        this.postRepository = postRepository;
        this.accountKpiRepository = accountKpiRepository;
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
    }

    @Transactional
    public void fetchLatestPosts() {
        try {
            log.info("Fetching latest posts from Instagram API for account: {}", businessAccountId);
            
            // Restoring the full field list now that the token issue is resolved.
            String mediaUrl = String.format("/%s/%s/media?fields=id,caption,media_type,timestamp,permalink,like_count,comments_count&access_token=%s",
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
                    
                    // Fetch and set detailed insights (reach, impressions, saves)
                    setPostInsights(post);

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