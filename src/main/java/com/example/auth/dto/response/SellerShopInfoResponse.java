package com.example.auth.dto.response;

import java.util.List;

public class SellerShopInfoResponse {
    private String shopName;
    private String shopDescription;
    private String shopAddress;
    private List<ProductSimpleInfo> products;

    public SellerShopInfoResponse(String shopName, String shopDescription, String shopAddress, List<ProductSimpleInfo> products) {
        this.shopName = shopName;
        this.shopDescription = shopDescription;
        this.shopAddress = shopAddress;
        this.products = products;
    }

    public String getShopName() { return shopName; }
    public String getShopDescription() { return shopDescription; }
    public String getShopAddress() { return shopAddress; }
    public List<ProductSimpleInfo> getProducts() { return products; }

    public static class ProductSimpleInfo {
        private Long productId;
        private String productName;
        private String productDescription;
        private Double productPrice;
        private String productStatus;

        public ProductSimpleInfo(Long productId, String productName, String productDescription, Double productPrice, String productStatus) {
            this.productId = productId;
            this.productName = productName;
            this.productDescription = productDescription;
            this.productPrice = productPrice;
            this.productStatus = productStatus;
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getProductDescription() { return productDescription; }
        public Double getProductPrice() { return productPrice; }
        public String getProductStatus() { return productStatus; }
    }
}