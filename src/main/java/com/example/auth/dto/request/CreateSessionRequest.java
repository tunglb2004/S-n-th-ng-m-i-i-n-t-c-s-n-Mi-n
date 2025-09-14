package com.example.auth.dto.request;

public class CreateSessionRequest {
    private Long toUserId;
    private String content; // Initial message

    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}