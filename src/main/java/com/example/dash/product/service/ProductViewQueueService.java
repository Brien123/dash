package com.example.dash.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductViewQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "product:view:queue";

    public void enqueueView(Long userId, String productId) {
        ProductViewTask task = new ProductViewTask(userId, productId);
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
    }
}
