package com.example.dash.product.mapper;

import com.example.dash.product.dto.CreateProductImageDto;
import com.example.dash.product.dto.ProductImageDto;
import com.example.dash.product.model.ProductImage;

public class ProductImageMapper {

    public static ProductImage toEntity(CreateProductImageDto dto) {
        ProductImage image = new ProductImage();
        image.setProductId(dto.getProductId());
        image.setImageUrl(dto.getImageUrl());
        return image;
    }

    public static ProductImageDto toDto(ProductImage image) {
        ProductImageDto dto = new ProductImageDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageThumbUrl(image.getImageThumbUrl());
        dto.setImageMediumUrl(image.getImageMediumUrl());
        dto.setCreatedAt(image.getCreatedAt());
        dto.setUpdatedAt(image.getUpdatedAt());
        return dto;
    }

    public static void updateEntity(ProductImage image, CreateProductImageDto dto) {
        image.setProductId(dto.getProductId());
        image.setImageUrl(dto.getImageUrl());
    }
}
