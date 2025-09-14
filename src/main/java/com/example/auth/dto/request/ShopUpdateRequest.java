// src/main/java/com/example/auth/dto/request/ShopUpdateRequest.java
package com.example.auth.dto.request;

public class ShopUpdateRequest {
    private String shopName;
    private String description;
    private String address;
    private String logo;

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

}