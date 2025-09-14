package com.example.auth.dto.request;

public class ProductUpdateRequest {
    private Long productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private Integer productStockQuantity;
    private Long provinceId;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }
    public Double getProductPrice() { return productPrice; }
    public void setProductPrice(Double productPrice) { this.productPrice = productPrice; }
    public Integer getProductStockQuantity() { return productStockQuantity; }
    public void setProductStockQuantity(Integer productStockQuantity) { this.productStockQuantity = productStockQuantity; }
    public Long getProvinceId() { return provinceId; }
    public void setProvinceId(Long provinceId) { this.provinceId = provinceId; }

}