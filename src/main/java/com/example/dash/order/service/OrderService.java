package com.example.dash.order.service;

import com.example.dash.order.dto.CreateOrderDto;
import com.example.dash.order.dto.OrderDto;
import com.example.dash.order.model.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDto createOrder(Long userId, CreateOrderDto createOrderDto);

    OrderDto getOrder(Long userId, String orderId);

    Page<OrderDto> getOrders(Long userId, Pageable pageable);

    void cancelOrder(Long userId, String orderId);

    OrderDto updateOrderStatus(String orderId, Status status);
}