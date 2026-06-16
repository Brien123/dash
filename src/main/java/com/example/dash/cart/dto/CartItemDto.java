package com.example.dash.cart.dto;

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
public class CartItemDto {
    private String id;
    private String cartId;
    private ProductDto product;
    private Long quantity;
    private Instant createdAt;
    private Instant updatedAt;
}
