package com.example.dash.cart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.dash.cart.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findByCartId(String cartId);

    Optional<CartItem> findByCartIdAndProductId(String cartId, String productId);

    void deleteByCartId(String cartId);
}