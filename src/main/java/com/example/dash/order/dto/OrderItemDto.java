package com.example.dash.order.dto;

import com.example.dash.product.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private String id;
    private ProductDto product;
    private Long quantity;
    private Double price;
    private Instant createdAt;
    private Instant updatedAt;
}
