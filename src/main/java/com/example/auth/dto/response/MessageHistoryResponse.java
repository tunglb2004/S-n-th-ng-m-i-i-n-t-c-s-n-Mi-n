package com.example.auth.dto.response;

import java.time.LocalDateTime;

public class MessageHistoryResponse {
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;

    public MessageHistoryResponse(Long senderId, String content, LocalDateTime createdAt) {
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getSenderId() { return senderId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}