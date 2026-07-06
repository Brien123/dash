package com.example.dash.common.Email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "email:queue";
    private static final long MAX_QUEUE_SIZE = 1000;

    public boolean enqueueOtpEmail(String to, String otp) {
        if (!hasRoom()) return false;
        EmailTask task = new EmailTask(to, otp, "otp");
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
        return true;
    }

    public boolean enqueuePasswordResetEmail(String to, String resetCode) {
        if (!hasRoom()) return false;
        EmailTask task = new EmailTask(to, resetCode, "password_reset");
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
        return true;
    }

    private boolean hasRoom() {
        Long size = redisTemplate.opsForList().size(QUEUE_KEY);
        if (size != null && size >= MAX_QUEUE_SIZE) {
            log.warn("Email queue is full ({} items). Dropping task.", size);
            return false;
        }
        return true;
    }
}
