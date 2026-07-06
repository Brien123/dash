package com.example.dash.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductViewQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "product:view:queue";
    private static final long MAX_QUEUE_SIZE = 5000;

    public boolean enqueueView(Long userId, String productId) {
        if (!hasRoom()) return false;
        ProductViewTask task = new ProductViewTask(userId, productId);
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
        return true;
    }

    private boolean hasRoom() {
        Long size = redisTemplate.opsForList().size(QUEUE_KEY);
        if (size != null && size >= MAX_QUEUE_SIZE) {
            log.warn("Product view queue is full ({} items). Dropping task.", size);
            return false;
        }
        return true;
    }
}
