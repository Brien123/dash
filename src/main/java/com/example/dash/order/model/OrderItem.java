package com.example.dash.order.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "order_items")
public class OrderItem {

    @Id
    private String id;

    @Indexed
    private String orderId;

    @Indexed
    private String productId;

    private Long quantity;

    private Double price;

    private Instant createdAt;

    private Instant updatedAt;
}