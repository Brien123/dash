package com.example.dash.product.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.web.PageableDefault;
import com.example.dash.common.dto.ApiResponse;
import com.example.dash.product.dto.CreateProductDto;
import com.example.dash.product.dto.ProductDto;
import com.example.dash.product.service.ProductService;
import com.example.dash.product.service.ProductViewQueueService;
import com.example.dash.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product/v1")
public class ProductController {

    private final ProductService productService;
    private final ProductViewQueueService productViewQueueService;
    private final UserRepository userRepository;

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> create(@ModelAttribute CreateProductDto createProductDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("product created successfully", productService.create(createProductDto)));
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> update(@PathVariable String productId, @ModelAttribute("product") CreateProductDto createProductDto){
        return ResponseEntity.ok(ApiResponse.success("product updated successfully", productService.update(productId, createProductDto)));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDto>> getById(@PathVariable String productId){
        Long userId = null;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName();
            userId = userRepository.findByEmail(email).map(com.example.dash.user.model.User::getId).orElse(null);
        }
        productViewQueueService.enqueueView(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("product gotten successfully", productService.getById(productId)));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAll(
            @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) Boolean isActive
    ){
        if (isActive != null) {
            return ResponseEntity.ok(ApiResponse.success("products gotten successfully", productService.getAllByStatus(isActive, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success("products gotten successfully", productService.getAll(pageable)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getProductByCategory(@PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable, @PathVariable String categoryId){
        return ResponseEntity.ok(ApiResponse.success("products gotten successfully", productService.getProductByCategory(categoryId, pageable)));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String productId){
         productService.delete(productId);
        return ResponseEntity.ok(ApiResponse.success("product deleted successfully"));
    }
	
}