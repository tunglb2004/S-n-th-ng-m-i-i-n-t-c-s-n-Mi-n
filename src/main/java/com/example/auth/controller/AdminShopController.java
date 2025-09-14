package com.example.auth.controller;

import com.example.auth.dto.response.ActiveShopInfoResponse;
import com.example.auth.dto.response.AdminProductInfoResponse;
import com.example.auth.entity.*;
import com.example.auth.repository.ShopRequestRepository;
import com.example.auth.repository.UserRepository;
import com.example.auth.repository.ShopRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import com.example.auth.dto.response.ShopRequestSimpleResponse;
import com.example.auth.dto.request.ShopIdRequest;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.entity.Product;
import com.example.auth.entity.Province;
import com.example.auth.entity.Shop;
import com.example.auth.entity.User;
import com.example.auth.repository.ProductRepository;
import com.example.auth.repository.ProvinceRepository;
import com.example.auth.dto.request.ProductIdRequest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/shops")
public class AdminShopController {

    @Autowired
    private ShopRequestRepository shopRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Operation(summary = "Admin duyệt yêu cầu mở gian hàng")
    @PostMapping("/review/{requestId}")
    public String reviewShopRequest(@PathVariable Long requestId, @RequestParam boolean approve) {
        ShopRequest request = shopRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));

        User user = request.getUser();
        if (user == null) {
            return "Không tìm thấy người dùng gửi yêu cầu!";
        }
        String userEmail = user.getEmail();
        String adminEmail = "luubachtung@gmail.com";

        if (approve) {
            request.setStatus("APPROVED");
            user.setRole("SELLER");
            userRepository.save(user);
            shopRequestRepository.save(request);

            // Tạo shop mới cho user
            Shop shop = new Shop();
            shop.setSeller(user);
            shop.setShopName(request.getShopName());
            shop.setDescription(request.getDescription());
            shop.setAddress(request.getAddress());
            shop.setStatus("ACTIVE");
            shopRepository.save(shop);

            sendMail(adminEmail, userEmail, "Yêu cầu mở gian hàng đã được chấp thuận!",
                "Chúc mừng! Yêu cầu mở gian hàng của bạn đã được duyệt. Bạn đã trở thành người bán.");
            return "Đã duyệt yêu cầu, cập nhật role SELLER và tạo shop.";
        } else {
            request.setStatus("REJECTED");
            shopRequestRepository.save(request);

            sendMail(adminEmail, userEmail, "Yêu cầu mở gian hàng bị từ chối",
                "Rất tiếc! Yêu cầu mở gian hàng của bạn đã bị từ chối.");
            return "Đã từ chối yêu cầu và gửi mail thông báo.";
        }
    }

    @Operation(summary = "Lấy danh sách tất cả yêu cầu mở gian hàng, ưu tiên PENDING trước, sau đó sắp xếp theo thời gian tạo")
    @GetMapping("/requests")
    public List<ShopRequestSimpleResponse> getAllShopRequestsSorted() {
        List<ShopRequest> allRequests = shopRequestRepository.findAll();
        return allRequests.stream()
                .sorted(Comparator.comparing((ShopRequest r) -> !"PENDING".equalsIgnoreCase(r.getStatus()))
                        .thenComparing(ShopRequest::getCreatedAt))
                .map(r -> new ShopRequestSimpleResponse(
                        r.getId(),
                        r.getShopName(),
                        r.getDescription(),
                        r.getAddress(),
                        r.getStatus(),
                        r.getUser() != null ? r.getUser().getId() : null
                ))
                .collect(Collectors.toList());
    }

    private void sendMail(String from, String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // Log lỗi gửi mail (nếu cần)
            System.out.println("Gửi mail thất bại: " + e.getMessage());
        }
    }

    @Operation(summary = "Admin khóa shop (chuyển trạng thái thành BLOCK)")
    @PostMapping("/block")
    public ApiResponse<String> blockShop(@RequestBody ShopIdRequest req) {
        Shop shop = shopRepository.findById(req.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        if ("BLOCK".equalsIgnoreCase(shop.getStatus())) {
            return new ApiResponse<>(400, "Shop is already blocked", null);
        }
        shop.setStatus("BLOCK");
        shopRepository.save(shop);
        return new ApiResponse<>(200, "Shop has been blocked", null);
    }

    @Operation(summary = "Admin mở lại shop (chuyển trạng thái thành ACTIVE)")
    @PostMapping("/unblock")
    public ApiResponse<String> unblockShop(@RequestBody ShopIdRequest req) {
        Shop shop = shopRepository.findById(req.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        if ("ACTIVE".equalsIgnoreCase(shop.getStatus())) {
            return new ApiResponse<>(400, "Shop is already active", null);
        }
        shop.setStatus("ACTIVE");
        shopRepository.save(shop);
        return new ApiResponse<>(200, "Shop has been unblocked", null);
    }

    @Operation(summary = "Admin xem danh sách tất cả các shop (cả BLOCK và ACTIVE)")
    @GetMapping("/all-shops")
    public List<ActiveShopInfoResponse> getAllShops() {
        List<Shop> allShops = shopRepository.findAll();
        return allShops.stream()
                .map(shop -> new ActiveShopInfoResponse(
                        shop.getShopName(),
                        shop.getAddress(),
                        shop.getCreatedAt(),
                        shop.getStatus(),
                        shop.getSeller().getId(),
                        shop.getSeller().getUsername()
                ))
                .collect(Collectors.toList());
    }

    @Operation(summary = "Admin xem tất cả sản phẩm đang bán")
    @GetMapping("/all-products")
    public List<AdminProductInfoResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(p -> {
            Double price = p.getPrice();
            Integer stock = p.getStockQuantity();
            Double finalPrice = price;
            if (stock != null && stock > 0) {
                finalPrice = price * (1 - stock / 100.0);
            }
            Province province = p.getProvince();
            Shop shop = p.getShop();
            User seller = shop.getSeller();
            return new AdminProductInfoResponse(
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    price,
                    stock,
                    finalPrice,
                    province != null ? province.getId() : null,
                    province != null ? province.getName() : null,
                    shop != null ? shop.getId() : null,
                    shop != null ? shop.getShopName() : null,
                    seller != null ? seller.getUsername() : null,
                    p.getStatus() // <-- Add this line
            );
        }).collect(Collectors.toList());
    }

    @Operation(summary = "Admin block a product (set status to BLOCK)")
    @PostMapping("/block-product")
    public ApiResponse<String> blockProduct(@RequestBody ProductIdRequest req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if ("BLOCK".equalsIgnoreCase(product.getStatus())) {
            return new ApiResponse<>(400, "Product is already blocked", null);
        }
        product.setStatus("BLOCK");
        productRepository.save(product);
        return new ApiResponse<>(200, "Product has been blocked", null);
    }

    @Operation(summary = "Admin unblock a product (set status to ACTIVE)")
    @PostMapping("/unblock-product")
    public ApiResponse<String> unblockProduct(@RequestBody ProductIdRequest req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if ("ACTIVE".equalsIgnoreCase(product.getStatus())) {
            return new ApiResponse<>(400, "Product is already active", null);
        }
        product.setStatus("ACTIVE");
        productRepository.save(product);
        return new ApiResponse<>(200, "Product has been unblocked", null);
    }
}