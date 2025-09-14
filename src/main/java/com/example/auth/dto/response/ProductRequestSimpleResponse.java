package com.example.auth.dto.response;

public class ProductRequestSimpleResponse {
    private String productName;
    private String description;
    private Double price;
    private Long shopId;
    private String provinceName;
    private String status;

    public ProductRequestSimpleResponse(String productName, String description, Double price, Long shopId, String provinceName, String status) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.shopId = shopId;
        this.provinceName = provinceName;
        this.status = status;
    }

    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Long getShopId() { return shopId; }
    public String getProvinceName() { return provinceName; }
    public String getStatus() { return status; }
}