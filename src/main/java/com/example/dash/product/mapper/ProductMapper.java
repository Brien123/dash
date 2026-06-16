package com.example.dash.product.mapper;

import com.example.dash.product.dto.CreateProductDto;
import com.example.dash.product.dto.ProductDto;
import com.example.dash.product.model.Product;

public class ProductMapper {

    public static Product toEntity(CreateProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCurrency(dto.getCurrency());
        product.setPrice(dto.getPrice());
        product.setCategoryId(dto.getCategoryId());
        product.setIsActive(dto.getIsActive());
        product.setStock(dto.getStock());
        return product;
    }

    public static ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCurrency(product.getCurrency());
        dto.setPrice(product.getPrice());
        dto.setCategoryId(product.getCategoryId());
        dto.setSlug(product.getSlug());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setIsActive(product.getIsActive());
        dto.setStock(product.getStock());
        return dto;
    }

    public static void updateEntity(Product product, CreateProductDto dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCurrency(dto.getCurrency());
        product.setPrice(dto.getPrice());
        product.setCategoryId(dto.getCategoryId());
        if (dto.getIsActive() != null) {
            product.setIsActive(dto.getIsActive());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }
    }
}
