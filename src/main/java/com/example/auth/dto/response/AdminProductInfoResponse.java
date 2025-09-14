package com.example.auth.dto.response;

public class AdminProductInfoResponse {
    private Long productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private Integer productStockQuantity;
    private Double productFinalPrice;
    private Long provinceId;
    private String provinceName;
    private Long shopId;
    private String shopName;
    private String sellerName;
    private String productStatus; // <-- Add this field

    public AdminProductInfoResponse(Long productId, String productName, String productDescription, Double productPrice,
                                    Integer productStockQuantity, Double productFinalPrice, Long provinceId, String provinceName,
                                    Long shopId, String shopName, String sellerName, String productStatus) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productStockQuantity = productStockQuantity;
        this.productFinalPrice = productFinalPrice;
        this.provinceId = provinceId;
        this.provinceName = provinceName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.sellerName = sellerName;
        this.productStatus = productStatus;
    }

    // Getters
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getProductDescription() { return productDescription; }
    public Double getProductPrice() { return productPrice; }
    public Integer getProductStockQuantity() { return productStockQuantity; }
    public Double getProductFinalPrice() { return productFinalPrice; }
    public Long getProvinceId() { return provinceId; }
    public String getProvinceName() { return provinceName; }
    public Long getShopId() { return shopId; }
    public String getShopName() { return shopName; }
    public String getSellerName() { return sellerName; }
    public String getProductStatus() { return productStatus; }
}