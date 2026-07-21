package com.example.dash.order.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
@Document(collection = "order")
public class Order {

    @Id
    private String id;

    @Indexed
    private Long userId;

    private Status status = Status.PENDING;

    private Double totalAmount;

    private List<OrderItem> items = new ArrayList<>();

    private Instant createdAt;

    private Instant updatedAt;
}
