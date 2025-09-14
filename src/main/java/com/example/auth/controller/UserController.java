package com.example.auth.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import com.example.auth.dto.response.UserProfileResponse;
import com.example.auth.dto.response.UserProfileResponse.OrderInfo;
import com.example.auth.entity.Order;
import com.example.auth.entity.User;
import com.example.auth.entity.UserProfile;
import com.example.auth.repository.OrderRepository;
import com.example.auth.repository.UserProfileRepository;
import com.example.auth.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Operation(summary = "Lấy thông tin cá nhân của người dùng hiện tại")
    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return null;
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUserId(user.getId());

        List<Order> orders = orderRepository.findByCustomerId(user.getId());
        List<OrderInfo> orderInfos = orders.stream()
            .map(order -> new OrderInfo(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalAmount()
            ))
            .collect(Collectors.toList());

        // Trả về đầy đủ thông tin user profile
        return new UserProfileResponse(
            profile != null ? profile.getFullName() : user.getUsername(),
            profile != null && profile.getBirthday() != null ?
                java.time.LocalDate.now().getYear() - profile.getBirthday().getYear() : null,
            user.getEmail(),
            profile != null ? profile.getAvatar() : null,
            profile != null ? profile.getAddress() : null,
            user.getPhone(),
            profile != null ? profile.getGender() : null,
            orderInfos
        );
    }
}