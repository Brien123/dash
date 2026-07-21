package com.example.dash.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dash.common.dto.ApiResponse;
import com.example.dash.order.dto.CreateOrderDto;
import com.example.dash.order.dto.OrderDto;
import com.example.dash.order.service.OrderService;
import com.example.dash.user.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order/v1")
public class OrderController {
    private final OrderService orderService;
    private final UserRepository userRepository;

    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@RequestBody CreateOrderDto createOrderDto){
        Long userId = getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("order created successfully", orderService.createOrder(userId, createOrderDto)));
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