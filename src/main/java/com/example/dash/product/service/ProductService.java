package com.example.dash.product.service;

import com.example.dash.product.dto.CreateProductDto;
import com.example.dash.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductDto create(CreateProductDto createProductDto);

    ProductDto getById(String productId);

    Page<ProductDto> getAll(Pageable pageable);

    Page<ProductDto> getAllByStatus(Boolean isActive, Pageable pageable);

    ProductDto update(String productId, CreateProductDto createProductDto);

    void delete(String productId);

    Page<ProductDto> getProductByCategory(String categoryId, Pageable pageable);
}
