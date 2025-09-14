package com.example.auth.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class UserProfileResponse {
    private String name;
    private Integer age;
    private String email;
    private String avatarUrl;
    private List<OrderInfo> orders;
    private String address;
    private String phone;
    private String gender;

    public UserProfileResponse(String name, Integer age, String email, String avatarUrl,
                              String address, String phone, String gender, List<OrderInfo> orders) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.address = address;
        this.phone = phone;
        this.gender = gender;
        this.orders = orders;
    }

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public List<OrderInfo> getOrders() { return orders; }
    public void setOrders(List<OrderInfo> orders) { this.orders = orders; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public static class OrderInfo {
        private Long orderId;
        private LocalDateTime orderDate;
        private Double totalAmount;

        public OrderInfo(Long orderId, LocalDateTime orderDate, Double totalAmount) {
            this.orderId = orderId;
            this.orderDate = orderDate;
            this.totalAmount = totalAmount;
        }

        // getters and setters
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public LocalDateTime getOrderDate() { return orderDate; }
        public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    }
}