package com.example.dash.search.controller;

import com.example.dash.common.dto.ApiResponse;
import com.example.dash.product.dto.ProductDto;
import com.example.dash.search.dto.SearchRequestDto;
import com.example.dash.search.service.ProductSearch;
import com.example.dash.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search/v1")
public class SearchController {
    private final ProductSearch productSearch;
    private final UserRepository userRepository;

    @PostMapping("/bulkindex")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> bulkIndexProducts(){
        productSearch.bulkIndexProducts();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("bulk index successful"));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> search(@RequestBody SearchRequestDto searchRequestDto,
                                                                @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable){
        Long userId = null;
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName();
            userId = userRepository.findByEmail(email).map(com.example.dash.user.model.User::getId).orElse(null);
        }
        Page<ProductDto> products = productSearch.search(searchRequestDto, userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("search was successful", products));

    }

}
