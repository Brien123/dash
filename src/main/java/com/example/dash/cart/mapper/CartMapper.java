package com.example.dash.cart.mapper;

import com.example.dash.cart.dto.AddToCartDto;
import com.example.dash.cart.dto.CartDto;
import com.example.dash.cart.dto.CartItemDto;
import com.example.dash.cart.dto.UpdateCartItemDto;
import com.example.dash.cart.model.Cart;
import com.example.dash.cart.model.CartItem;
import com.example.dash.product.dto.ProductDto;

public class CartMapper {

    public static CartDto toCartDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setCreatedAt(cart.getCreatedAt());
        dto.setUpdatedAt(cart.getUpdatedAt());
        return dto;
    }

    public static CartItemDto toCartItemDto(CartItem cartItem, ProductDto productDto) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setCartId(cartItem.getCartId());
        dto.setProduct(productDto);
        dto.setQuantity(cartItem.getQuantity());
        dto.setCreatedAt(cartItem.getCreatedAt());
        dto.setUpdatedAt(cartItem.getUpdatedAt());
        return dto;
    }

    public static CartItem toEntity(AddToCartDto dto, String cartId) {
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cartId);
        cartItem.setProductId(dto.getProductId());
        cartItem.setQuantity(dto.getQuantity());
        return cartItem;
    }

    public static void updateEntity(CartItem cartItem, UpdateCartItemDto dto) {
        if (dto.getQuantity() != null) {
            cartItem.setQuantity(dto.getQuantity());
        }
    }
}
