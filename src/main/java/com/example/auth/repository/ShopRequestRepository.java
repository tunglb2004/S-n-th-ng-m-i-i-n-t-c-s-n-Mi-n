package com.example.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.ShopRequest;

public interface ShopRequestRepository extends JpaRepository<ShopRequest, Long> {
    List<ShopRequest> findByStatus(String status);
    boolean existsByUserId(Long userId);
    boolean existsByUserIdAndStatusIn(Long userId, List<String> statuses);
}