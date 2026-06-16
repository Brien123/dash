package com.example.dash.common.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "image:queue";

    public void enqueueCompression(String storedPath, String entityType, String entityId) {
        ImageTask task = new ImageTask(storedPath, "compress", entityType, entityId);
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
    }
}
