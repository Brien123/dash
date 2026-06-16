package com.example.dash.product.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class CreateProductImageDto {
    private String productId;
    private String imageUrl;
    private MultipartFile file;
}
