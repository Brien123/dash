package com.example.dash.common.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "image:queue";
    private static final long MAX_QUEUE_SIZE = 500;

    public boolean enqueueCompression(String storedPath, String entityType, String entityId) {
        if (!hasRoom()) return false;
        ImageTask task = new ImageTask(storedPath, "compress", entityType, entityId);
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
        return true;
    }

    private boolean hasRoom() {
        Long size = redisTemplate.opsForList().size(QUEUE_KEY);
        if (size != null && size >= MAX_QUEUE_SIZE) {
            log.warn("Image queue is full ({} items). Dropping task.", size);
            return false;
        }
        return true;
    }
}
