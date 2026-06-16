package com.example.dash.common.Email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "email:queue";

    public void enqueueOtpEmail(String to, String otp) {
        EmailTask task = new EmailTask(to, otp, "otp");
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
    }

    public void enqueuePasswordResetEmail(String to, String resetCode) {
        EmailTask task = new EmailTask(to, resetCode, "password_reset");
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
    }
}
