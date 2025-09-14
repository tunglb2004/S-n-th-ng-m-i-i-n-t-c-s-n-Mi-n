package com.example.auth.controller;

import com.example.auth.dto.request.*;
import com.example.auth.dto.response.ApiResponse;
import com.example.auth.dto.response.ProductDetailResponse;
import com.example.auth.entity.*;
import com.example.auth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.example.auth.dto.response.ProductRequestSimpleResponse;
import com.example.auth.dto.request.ProductIdRequest;
import com.example.auth.entity.Shop;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private ProductRequestRepository productRequestRepository;
    @Autowired
    private ProductRepository productRepository;

    // Seller gửi yêu cầu thêm sản phẩm
    @Operation(summary = "Seller gửi yêu cầu thêm sản phẩm")
    @PostMapping("/submit")
    public ApiResponse<String> submitProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductSubmitRequest req) {
        if (userDetails == null) {
            return new ApiResponse<>(401, "Authentication required!", null);
        }
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!"SELLER".equalsIgnoreCase(user.getRole())) {
            return new ApiResponse<>(403, "Only SELLER can submit products!", null);
        }
        Shop shop = shopRepository.findBySellerId(user.getId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));
        Province province = provinceRepository.findById(req.getProvinceId())
                .orElseThrow(() -> new RuntimeException("Province not found"));

        ProductRequest productRequest = new ProductRequest();
        productRequest.setShop(shop);
        productRequest.setName(req.getName());
        productRequest.setPrice(req.getPrice());
        productRequest.setProvince(province);
        productRequest.setDescription(req.getDescription());
        productRequest.setStatus("PENDING");
        productRequestRepository.save(productRequest);

        return new ApiResponse<>(200, "Product request submitted!", null);
    }

    // Admin duyệt sản phẩm
    @Operation(summary = "Admin duyệt sản phẩm")
    @PostMapping("/approve")
    public ApiResponse<String> approveProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductApproveRequest req) {
        if (userDetails == null) {
            return new ApiResponse<>(401, "Authentication required!", null);
        }
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return new ApiResponse<>(403, "Only ADMIN can approve products!", null);
        }
        ProductRequest productRequest = productRequestRepository.findById(req.getProductRequestId())
                .orElseThrow(() -> new RuntimeException("Product request not found"));

        if ("PASS".equalsIgnoreCase(req.getAction())) {
            Product product = new Product();
            product.setShop(productRequest.getShop());
            product.setName(productRequest.getName());
            product.setPrice(productRequest.getPrice());
            product.setProvince(productRequest.getProvince());
            product.setDescription(productRequest.getDescription());
            product.setStatus("ACTIVE");
            productRepository.save(product);

            productRequest.setStatus("PASS");
            productRequestRepository.save(productRequest);

            return new ApiResponse<>(200, "Product approved and published!", null);
        } else if ("FAIL".equalsIgnoreCase(req.getAction())) {
            productRequest.setStatus("FAIL");
            productRequestRepository.save(productRequest);
            return new ApiResponse<>(200, "Product request rejected!", null);
        } else {
            return new ApiResponse<>(400, "Invalid action!", null);
        }
    }
    // Admin xem tất cả yêu cầu sản phẩm, sắp xếp PENDING lên đầu, trong PENDING sắp xếp theo thời gian tạo
    @Operation(summary = "Admin xem tất cả yêu cầu sản phẩm, sắp xếp PENDING lên đầu, trong PENDING sắp xếp theo thời gian tạo")
    @GetMapping("/requests")
    public ApiResponse<List<ProductRequestSimpleResponse>> getAllProductRequestsSorted(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return new ApiResponse<>(401, "Authentication required!", null);
        }
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ProductRequest> requests;

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            requests = productRequestRepository.findAll();
        } else if ("SELLER".equalsIgnoreCase(user.getRole())) {
            Shop shop = shopRepository.findBySellerId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            requests = productRequestRepository.findAll().stream()
                    .filter(r -> r.getShop() != null && shop.getId().equals(r.getShop().getId()))
                    .collect(Collectors.toList());
        } else {
            return new ApiResponse<>(403, "You do not have permission to view product requests!", null);
        }

        List<ProductRequestSimpleResponse> result = requests.stream()
                .sorted(
                        Comparator.comparing((ProductRequest r) -> !"PENDING".equalsIgnoreCase(r.getStatus()))
                                .thenComparing(ProductRequest::getCreatedAt)
                )
                .map(r -> new ProductRequestSimpleResponse(
                        r.getName(),
                        r.getDescription(),
                        r.getPrice(),
                        r.getShop() != null ? r.getShop().getId() : null,
                        r.getProvince() != null ? r.getProvince().getName() : null,
                        r.getStatus()
                ))
                .collect(Collectors.toList());

        return new ApiResponse<>(200, "Success", result);
    }
    // In `ProductController.java`

    @Operation(summary = "Update product info")
    @PutMapping("/update")
    public ApiResponse<String> updateProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductUpdateRequest req) {
        if (userDetails == null) return new ApiResponse<>(401, "Authentication required!", null);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getShop().getSeller().getId().equals(user.getId()))
            return new ApiResponse<>(403, "Not your product!", null);

        product.setName(req.getProductName());
        product.setDescription(req.getProductDescription());
        product.setPrice(req.getProductPrice());
        product.setStockQuantity(req.getProductStockQuantity());
        Province province = provinceRepository.findById(req.getProvinceId())
                .orElseThrow(() -> new RuntimeException("Province not found"));
        product.setProvince(province);
        productRepository.save(product);

        return new ApiResponse<>(200, "Product updated!", null);
    }

    @Operation(summary = "Ân sản phẩm (đặt trạng thái thành STOP)")
    @PostMapping("/hide")
    public ApiResponse<String> hideProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductIdRequest req) {
        return updateProductStatus(userDetails, req.getProductId(), "STOP");
    }

    @Operation(summary = "Hiện sản phẩm (đặt trạng thái thành ACTIVE)")
    @PostMapping("/unhide")
    public ApiResponse<String> unhideProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductIdRequest req) {
        return updateProductStatus(userDetails, req.getProductId(), "ACTIVE");
    }

    private ApiResponse<String> updateProductStatus(UserDetails userDetails, Long productId, String status) {
        if (userDetails == null) return new ApiResponse<>(401, "Authentication required!", null);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getShop().getSeller().getId().equals(user.getId()))
            return new ApiResponse<>(403, "Not your product!", null);

        product.setStatus(status);
        productRepository.save(product);
        return new ApiResponse<>(200, "Product status updated!", null);
    }

    @Operation(summary = "Delete product")
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductIdRequest req) {
        if (userDetails == null) return new ApiResponse<>(401, "Authentication required!", null);
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getShop().getSeller().getId().equals(user.getId()))
            return new ApiResponse<>(403, "Not your product!", null);

        productRepository.delete(product);
        return new ApiResponse<>(200, "Product deleted!", null);
    }

    @Operation(summary = "Xem chi tiết sản phẩm")
    @PostMapping("/detail")
    public ApiResponse<ProductDetailResponse> getProductDetail(@RequestBody ProductDetailRequest req) {
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Shop shop = product.getShop();
        Province province = product.getProvince();
        Double price = product.getPrice() != null ? product.getPrice() : 0.0;
        Integer stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        Double finalPrice = price * (1 - stock / 100.0);
        ProductDetailResponse response = new ProductDetailResponse(
                product.getName(),
                product.getDescription(),
                price,
                stock,
                finalPrice,
                shop != null ? shop.getId() : null,
                shop != null ? shop.getShopName() : null, // <-- FIXED HERE
                province != null ? province.getId() : null,
                province != null ? province.getName() : null
        );
        return new ApiResponse<>(200, "OK", response);
    }
}