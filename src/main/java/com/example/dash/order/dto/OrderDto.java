package com.example.dash.order.dto;

import com.example.dash.order.model.Status;

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
public class OrderDto {
    private String id;
    private Long userId;
    private Status status;
    private Double totalAmount;
    private List<OrderItemDto> items = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}
