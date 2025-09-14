package com.example.auth.dto.request;

public class ProductApproveRequest {
    private Long productRequestId;
    private String action; // PASS or FAIL

    // getters and setters
    public Long getProductRequestId() { return productRequestId; }
    public void setProductRequestId(Long productRequestId) { this.productRequestId = productRequestId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}