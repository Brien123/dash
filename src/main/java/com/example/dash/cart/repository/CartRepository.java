package com.example.dash.cart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.dash.cart.model.Cart;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(Long userId);
}