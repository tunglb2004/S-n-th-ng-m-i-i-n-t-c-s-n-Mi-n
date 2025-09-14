package com.example.auth.dto.response;

import java.time.LocalDateTime;

public class ActiveShopInfoResponse {
    private String shopName;
    private String shopAddress;
    private LocalDateTime shopCreatedAt;
    private String shopStatus;
    private Long shopSellerId;
    private String shopSellerName;

    public ActiveShopInfoResponse(String shopName, String shopAddress, LocalDateTime shopCreatedAt, String shopStatus, Long shopSellerId, String shopSellerName) {
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.shopCreatedAt = shopCreatedAt;
        this.shopStatus = shopStatus;
        this.shopSellerId = shopSellerId;
        this.shopSellerName = shopSellerName;
    }

    public String getShopName() { return shopName; }
    public String getShopAddress() { return shopAddress; }
    public LocalDateTime getShopCreatedAt() { return shopCreatedAt; }
    public String getShopStatus() { return shopStatus; }
    public Long getShopSellerId() { return shopSellerId; }
    public String getShopSellerName() { return shopSellerName; }
}