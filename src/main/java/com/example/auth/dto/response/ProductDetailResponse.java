package com.example.auth.dto.response;

public class ProductDetailResponse {
    private String productName;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Double finalPrice;
    private Long shopId;
    private String shopName;
    private Long provinceId;
    private String provinceName;

    public ProductDetailResponse(String productName, String description, Double price, Integer stockQuantity, Double finalPrice,
                                 Long shopId, String shopName, Long provinceId, String provinceName) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.finalPrice = finalPrice;
        this.shopId = shopId;
        this.shopName = shopName;
        this.provinceId = provinceId;
        this.provinceName = provinceName;
    }

    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public Double getFinalPrice() { return finalPrice; }
    public Long getShopId() { return shopId; }
    public String getShopName() { return shopName; }
    public Long getProvinceId() { return provinceId; }
    public String getProvinceName() { return provinceName; }
}