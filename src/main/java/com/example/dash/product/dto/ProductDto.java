package com.example.dash.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;
import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String id;
    private String name;
    private String description;
    private String currency="XAF";
    private Double price;
    private String categoryId;
    private String slug;
    private ArrayList<ProductImageDto> images;
    private Instant createdAt;
    private Instant updatedAt;

    private Boolean isActive;

    private Integer stock;
}