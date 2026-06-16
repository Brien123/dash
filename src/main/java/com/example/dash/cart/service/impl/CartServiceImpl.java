package com.example.dash.cart.service.impl;

import com.example.dash.cart.dto.AddToCartDto;
import com.example.dash.cart.dto.CartDto;
import com.example.dash.cart.dto.CartItemDto;
import com.example.dash.cart.dto.UpdateCartItemDto;
import com.example.dash.cart.mapper.CartMapper;
import com.example.dash.cart.model.Cart;
import com.example.dash.cart.model.CartItem;
import com.example.dash.cart.repository.CartItemRepository;
import com.example.dash.cart.repository.CartRepository;
import com.example.dash.cart.service.CartService;
import com.example.dash.product.dto.ProductDto;
import com.example.dash.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service("cart")
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    @Override
    public CartDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found for user: " + userId));
        return buildCartDto(cart);
    }

    @Override
    public CartDto getOrCreateCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setCreatedAt(Instant.now());
                    newCart.setUpdatedAt(Instant.now());
                    return cartRepository.save(newCart);
                });
        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto addItem(Long userId, AddToCartDto addToCartDto) {
        // ProductDto productDto = productService.getById(addToCartDto.getProductId());

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setCreatedAt(Instant.now());
                    newCart.setUpdatedAt(Instant.now());
                    return cartRepository.save(newCart);
                });

        cartItemRepository.findByCartIdAndProductId(cart.getId(), addToCartDto.getProductId())
                .ifPresentOrElse(
                        existing -> {
                            existing.setQuantity(existing.getQuantity() + addToCartDto.getQuantity());
                            existing.setUpdatedAt(Instant.now());
                            cartItemRepository.save(existing);
                        },
                        () -> {
                            CartItem cartItem = CartMapper.toEntity(addToCartDto, cart.getId());
                            cartItem.setCreatedAt(Instant.now());
                            cartItem.setUpdatedAt(Instant.now());
                            cartItemRepository.save(cartItem);
                        });

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateItemQuantity(Long userId, String itemId, UpdateCartItemDto updateCartItemDto) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found for user: " + userId));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Cart item not found: " + itemId));

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }

        CartMapper.updateEntity(cartItem, updateCartItemDto);
        cartItem.setUpdatedAt(Instant.now());
        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public CartDto removeItem(Long userId, String itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found for user: " + userId));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Cart item not found: " + itemId));

        if (!cartItem.getCartId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }

        cartItemRepository.delete(cartItem);

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);

        return buildCartDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found for user: " + userId));

        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.delete(cart);
    }

    private CartDto buildCartDto(Cart cart) {
        CartDto dto = CartMapper.toCartDto(cart);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemDto> itemDtos = new ArrayList<>();
        for (CartItem item : items) {
            ProductDto productDto = null;
            try {
                productDto = productService.getById(item.getProductId());
            } catch (NoSuchElementException e) {
                // product deleted; item stays in cart with null product
            }
            itemDtos.add(CartMapper.toCartItemDto(item, productDto));
        }
        dto.setItems(itemDtos);
        return dto;
    }
}
