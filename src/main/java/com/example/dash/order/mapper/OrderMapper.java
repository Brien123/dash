package com.example.dash.order.mapper;

import com.example.dash.order.dto.CreateOrderDto;
import com.example.dash.order.dto.CreateOrderItemDto;
import com.example.dash.order.dto.OrderDto;
import com.example.dash.order.dto.OrderItemDto;
import com.example.dash.order.model.Order;
import com.example.dash.order.model.OrderItem;
import com.example.dash.order.model.Status;
import com.example.dash.product.dto.ProductDto;

public class OrderMapper {

    public static OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    public static OrderItemDto toOrderItemDto(OrderItem orderItem, ProductDto productDto) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(orderItem.getId());
        dto.setProduct(productDto);
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        dto.setCreatedAt(orderItem.getCreatedAt());
        dto.setUpdatedAt(orderItem.getUpdatedAt());
        return dto;
    }

    public static Order toEntity(CreateOrderDto createOrderDto, Long userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(Status.PENDING);
        return order;
    }

    public static OrderItem toEntity(CreateOrderItemDto createOrderItemDto, String orderId) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setProductId(createOrderItemDto.getProductId());
        orderItem.setQuantity(createOrderItemDto.getQuantity());
        return orderItem;
    }
}
