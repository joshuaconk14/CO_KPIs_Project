package com.instagram.kpi.service;

import java.time.LocalDateTime;

public class TestData {
    private String message;
    private int count;
    private LocalDateTime timestamp;

    public TestData() {
    }

    public TestData(String message, int count) {
        this.message = message;
        this.count = count;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 