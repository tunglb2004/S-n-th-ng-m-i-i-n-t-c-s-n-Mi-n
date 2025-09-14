package com.example.auth.dto.request;

public class ApproveShopRequest {
    private Long requestId;
    private String action; // PASS or FAIL

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}