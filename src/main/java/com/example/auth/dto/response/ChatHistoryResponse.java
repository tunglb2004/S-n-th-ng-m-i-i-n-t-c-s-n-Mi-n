package com.example.auth.dto.response;

import java.util.List;

public class ChatHistoryResponse {
    private Long userId;
    private String username;
    private List<MessageHistoryResponse> messages;

    public ChatHistoryResponse(Long userId, String username, List<MessageHistoryResponse> messages) {
        this.userId = userId;
        this.username = username;
        this.messages = messages;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public List<MessageHistoryResponse> getMessages() { return messages; }
}