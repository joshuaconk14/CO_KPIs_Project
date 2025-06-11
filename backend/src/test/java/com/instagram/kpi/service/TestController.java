package com.instagram.kpi.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    private final SimpMessagingTemplate messagingTemplate;
    private int counter = 0;

    public TestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/data")
    public TestData getTestData() {
        return new TestData("Test message", counter++);
    }

    @PostMapping("/send")
    public void sendTestMessage(@RequestBody TestData testData) {
        messagingTemplate.convertAndSend("/topic/kpi-updates", testData);
    }
} 