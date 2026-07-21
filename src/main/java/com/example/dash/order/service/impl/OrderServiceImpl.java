package com.example.dash.order.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.dash.order.service.OrderService;
import com.example.dash.user.model.User;
import com.example.dash.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.example.dash.order.dto.CreateOrderDto;
import com.example.dash.order.dto.OrderDto;
import com.example.dash.order.mapper.OrderMapper;
import com.example.dash.order.model.Order;
import com.example.dash.order.model.OrderItem;
import com.example.dash.order.model.Status;
import com.example.dash.order.repository.OrderRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(Long userId, CreateOrderDto createOrderDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Order order = OrderMapper.toEntity(createOrderDto, user.getId());
        
        List<OrderItem> orderItems = createOrderDto.getItems().stream()
                .map(itemDto -> {
                    OrderItem item = OrderMapper.toEntity(itemDto, order.getId());
                    return item;
                })
                .collect(Collectors.toList());
        order.setItems(orderItems);
    
        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toOrderDto(savedOrder);
    }


    public OrderDto getOrder(Long userId, String orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new NoSuchElementException("Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this order");
        }
        return OrderMapper.toOrderDto(order);
    };

    public Page<OrderDto> getOrders(Long userId, Pageable pageable){
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(OrderMapper::toOrderDto);
    };

    public void cancelOrder(Long userId, String orderId){
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
            .orElseThrow(()-> new NoSuchElementException("Order not found"));
        order.setStatus(Status.CANCELED);
        orderRepository.save(order);
    };

    public OrderDto updateOrderStatus(String orderId, Status status){
        Order order = orderRepository.findById(orderId)
            .orElseThrow(()-> new NoSuchElementException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
        return OrderMapper.toOrderDto(order);
    };
}