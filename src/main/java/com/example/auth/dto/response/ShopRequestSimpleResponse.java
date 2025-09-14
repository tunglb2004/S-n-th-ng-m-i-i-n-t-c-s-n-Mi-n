package com.example.auth.dto.response;

public class ShopRequestSimpleResponse {
    private Long requestId;
    private String shopName;
    private String description;
    private String address;
    private String status;
    private Long userId;

    public ShopRequestSimpleResponse(Long requestId, String shopName, String description, String address, String status, Long userId) {
        this.requestId = requestId;
        this.shopName = shopName;
        this.description = description;
        this.address = address;
        this.status = status;
        this.userId = userId;
    }

    public Long getRequestId() { return requestId; }
    public String getShopName() { return shopName; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getStatus() { return status; }
    public Long getUserId() { return userId; }
}