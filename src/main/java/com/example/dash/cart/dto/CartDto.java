package com.example.dash.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private String id;
    private Long userId;
    private List<CartItemDto> items = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}
