package com.example.auth.dto.response;

public class ShopOpenResponse {
    private Long userId;
    private String shopName;
    private String address;
    private String description;
    private String message;

    public ShopOpenResponse(Long userId, String shopName, String address, String description, String message) {
        this.userId = userId;
        this.shopName = shopName;
        this.address = address;
        this.description = description;
        this.message = message;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}