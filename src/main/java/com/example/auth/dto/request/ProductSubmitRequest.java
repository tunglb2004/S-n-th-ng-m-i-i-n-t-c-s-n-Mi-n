package com.example.auth.dto.request;

public class ProductSubmitRequest {
    private String name;
    private Double price;
    private Long provinceId;
    private String description;

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Long getProvinceId() { return provinceId; }
    public void setProvinceId(Long provinceId) { this.provinceId = provinceId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}