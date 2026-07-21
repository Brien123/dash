package com.example.dash.order.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.dash.order.model.OrderItem;

public interface OrderItemRepository extends MongoRepository<OrderItem, String> {
}