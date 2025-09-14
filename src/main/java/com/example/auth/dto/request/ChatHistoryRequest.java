package com.example.auth.dto.request;

public class ChatHistoryRequest {
    private Long userId;

    public ChatHistoryRequest() {}

    public ChatHistoryRequest(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}