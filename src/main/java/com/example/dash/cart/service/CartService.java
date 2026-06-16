package com.example.dash.cart.service;

import com.example.dash.cart.dto.AddToCartDto;
import com.example.dash.cart.dto.CartDto;
import com.example.dash.cart.dto.UpdateCartItemDto;

public interface CartService {

    CartDto getOrCreateCart(Long userId);

    CartDto getCart(Long userId);

    CartDto addItem(Long userId, AddToCartDto addToCartDto);

    CartDto updateItemQuantity(Long userId, String itemId, UpdateCartItemDto updateCartItemDto);

    CartDto removeItem(Long userId, String itemId);

    void clearCart(Long userId);
}