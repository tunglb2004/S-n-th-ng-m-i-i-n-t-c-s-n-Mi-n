package com.example.auth.dto.request;

public class ProductDetailRequest {
    private Long productId;

    public ProductDetailRequest() {}
    public ProductDetailRequest(Long productId) { this.productId = productId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
}