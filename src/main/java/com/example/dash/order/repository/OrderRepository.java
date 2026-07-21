package com.example.dash.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.dash.order.model.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Optional<Order> findByUserIdAndId(Long userId, String orderId);
}