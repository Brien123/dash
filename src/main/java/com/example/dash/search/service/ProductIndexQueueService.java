package com.example.dash.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductIndexQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "product:index:queue";

    public void enqueueIndex(String productId) {
        ProductIndexTask task = new ProductIndexTask(productId, "index");
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
    }

    public void enqueueDelete(String productId) {
        ProductIndexTask task = new ProductIndexTask(productId, "delete");
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
    }
}