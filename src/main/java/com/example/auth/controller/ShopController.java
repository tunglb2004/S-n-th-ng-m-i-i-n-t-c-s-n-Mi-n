package com.example.auth.controller;

import com.example.auth.dto.request.ApproveShopRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.ShopOpenResponse;
import com.example.auth.entity.Shop;
import com.example.auth.repository.ShopRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.auth.dto.request.ShopOpenRequest;
import com.example.auth.entity.ShopRequest;
import com.example.auth.entity.User;
import com.example.auth.repository.ShopRequestRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.dto.response.SellerShopInfoResponse;
import com.example.auth.dto.request.ShopUpdateRequest;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopRequestRepository shopRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Operation(summary = "Người dùng gửi yêu cầu mở gian hàng")
    @PostMapping("/request")
    public ApiResponse<ShopOpenResponse> requestShop(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ShopOpenRequest req) {
        if (userDetails == null) {
            return new ApiResponse<>(401, "Authentication required!", null);
        }
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!"USER".equalsIgnoreCase(user.getRole())) {
            return new ApiResponse<>(403, "Bạn không thể đăng ký mở gian hàng!",
                    new ShopOpenResponse(user.getId(), req.getShopName(), req.getAddress(), req.getDescription(), "Bạn không thể đăng ký mở gian hàng!"));
        }
        boolean exists = shopRequestRepository.existsByUserIdAndStatusIn(
                user.getId(), List.of("PENDING", "PASS"));
        if (exists) {
            return new ApiResponse<>(409, "Bạn đã gửi yêu cầu mở gian hàng trước đó!",
                    new ShopOpenResponse(user.getId(), req.getShopName(), req.getAddress(), req.getDescription(), "Bạn đã gửi yêu cầu mở gian hàng trước đó!"));
        }
        ShopRequest shopRequest = new ShopRequest();
        shopRequest.setUser(user);
        shopRequest.setShopName(req.getShopName());
        shopRequest.setAddress(req.getAddress());
        shopRequest.setDescription(req.getDescription());
        shopRequest.setStatus("PENDING");
        shopRequestRepository.save(shopRequest);
        return new ApiResponse<>(200, "Yêu cầu mở gian hàng đã được gửi!",
                new ShopOpenResponse(user.getId(), req.getShopName(), req.getAddress(), req.getDescription(), "Yêu cầu mở gian hàng đã được gửi!"));
    }

    @Operation(summary = "Admin duyệt yêu cầu mở gian hàng")
    @PostMapping("/approve")
    public ApiResponse<String> approveShopRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ApproveShopRequest req) {
        if (userDetails == null) {
            return new ApiResponse<>(401, "Authentication required!", null);
        }
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return new ApiResponse<>(403, "Bạn không có quyền duyệt yêu cầu!", null);
        }

        ShopRequest shopRequest = shopRequestRepository.findById(req.getRequestId())
                .orElseThrow(() -> new RuntimeException("Shop request not found"));

        User user = shopRequest.getUser();

        if ("PASS".equalsIgnoreCase(req.getAction())) {
            Shop shop = new Shop();
            shop.setSeller(user);
            shop.setShopName(shopRequest.getShopName());
            shop.setAddress(shopRequest.getAddress());
            shop.setDescription(shopRequest.getDescription());
            shop.setCreatedAt(java.time.LocalDateTime.now());
            shop.setUpdatedAt(shop.getCreatedAt());
            shopRepository.save(shop);

            shopRequest.setStatus("PASS");
            shopRequestRepository.save(shopRequest);

            user.setRole("SELLER");
            userRepository.save(user);

            return new ApiResponse<>(200, "Phê duyệt thành công, gian hàng đã được tạo!", null);
        } else if ("FAIL".equalsIgnoreCase(req.getAction())) {
            shopRequest.setStatus("FAIL");
            shopRequestRepository.save(shopRequest);
            return new ApiResponse<>(200, "Yêu cầu mở gian hàng đã bị từ chối!", null);
        } else {
            return new ApiResponse<>(400, "Hành động không hợp lệ!", null);
        }
    }

    // Người bán xem thông tin gian hàng của mình
    @Operation(summary = "Người bán xem thông tin gian hàng của mình")
    @GetMapping("/my-info")
    public ApiResponse<SellerShopInfoResponse> getMyShopInfo(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return new ApiResponse<>(401, "Authentication required!", null);
        }
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!"SELLER".equalsIgnoreCase(user.getRole())) {
            return new ApiResponse<>(403, "Only SELLER can view shop info!", null);
        }
        Shop shop = shopRepository.findBySellerId(user.getId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        List<SellerShopInfoResponse.ProductSimpleInfo> products = shop.getProducts().stream()
                .map(p -> {
                    Double price = p.getPrice();
                    Integer stockQuantity = p.getStockQuantity();
                    Double finalPrice = price;
                    if (stockQuantity != null && stockQuantity > 0) {
                        finalPrice = price * (1 - stockQuantity / 100.0);
                    }
                    return new SellerShopInfoResponse.ProductSimpleInfo(
                            p.getId(),
                            p.getName(),
                            p.getDescription(),
                            finalPrice,
                            p.getStatus() // Add status here
                    );
                })
                .collect(Collectors.toList());

        SellerShopInfoResponse response = new SellerShopInfoResponse(
                shop.getShopName(),
                shop.getDescription(),
                shop.getAddress(),
                products
        );
        return new ApiResponse<>(200, "Success", response);
    }
    @Operation(summary = "Người bán cập nhật thông tin gian hàng của mình")
    @PutMapping("/update")
    public ApiResponse<String> updateShopInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ShopUpdateRequest req) {
        if (userDetails == null) return new ApiResponse<>(401, "Authentication required!", null);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Shop shop = shopRepository.findBySellerId(user.getId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        shop.setShopName(req.getShopName());
        shop.setDescription(req.getDescription());
        shop.setAddress(req.getAddress());
        shop.setLogo(req.getLogo());
        shopRepository.save(shop);

        return new ApiResponse<>(200, "Shop info updated!", null);
    }
}