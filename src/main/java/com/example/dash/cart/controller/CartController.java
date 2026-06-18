package com.example.dash.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dash.cart.dto.AddToCartDto;
import com.example.dash.cart.dto.CartDto;
import com.example.dash.cart.dto.UpdateCartItemDto;
import com.example.dash.cart.service.CartService;
import com.example.dash.common.dto.ApiResponse;
import com.example.dash.user.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart/v1")
public class CartController {

	private final CartService cartService;
	private final UserRepository userRepository;

	@GetMapping("/")
	public ResponseEntity<ApiResponse<CartDto>> getCart(){
		Long userId = getCurrentUserId();
		return ResponseEntity.ok(ApiResponse.success("Cart gotten successfully", cartService.getOrCreateCart(userId)));
	}

	@PostMapping("/")
	public ResponseEntity<ApiResponse<CartDto>> addItem(@RequestBody AddToCartDto addToCartDto){
		Long userId = getCurrentUserId();
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Item added to cart successfully", cartService.addItem(userId, addToCartDto)));
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<ApiResponse<CartDto>> updateItemQuantity(@PathVariable String itemId, @RequestBody UpdateCartItemDto updateCartItemDto){
		Long userId = getCurrentUserId();
		return ResponseEntity.ok(ApiResponse.success("Cart item updated successfully", cartService.updateItemQuantity(userId, itemId, updateCartItemDto)));
	}

	@DeleteMapping("/{itemId}")
	public ResponseEntity<ApiResponse<Void>> removeItem(@PathVariable String itemId){
		Long userId = getCurrentUserId();
		cartService.removeItem(userId, itemId);
		return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully"));
	}

	@DeleteMapping("/")
	public ResponseEntity<ApiResponse<Void>> clearCart(){
		Long userId = getCurrentUserId();
		cartService.clearCart(userId);
		return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
	}

	private Long getCurrentUserId() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getName())) {
			String email = authentication.getName();
			return userRepository.findByEmail(email).map(com.example.dash.user.model.User::getId).orElse(null);
		}
		return null;
	}

}