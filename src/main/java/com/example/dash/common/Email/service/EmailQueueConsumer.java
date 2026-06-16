package com.example.dash.common.Email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;

    private static final String QUEUE_KEY = "email:queue";

    @Scheduled(fixedDelay = 1000)
    public void processQueue() {
        EmailTask task = (EmailTask) redisTemplate.opsForList().leftPop(QUEUE_KEY);
        if (task != null) {
            try {
                switch (task.getType()) {
                    case "password_reset" -> {
                        emailService.sendPasswordResetEmail(task.getTo(), task.getCode());
                        log.info("Password reset email sent to {}", task.getTo());
                    }
                    default -> {
                        emailService.sendOtpEmail(task.getTo(), task.getCode());
                        log.info("OTP email sent to {}", task.getTo());
                    }
                }
            } catch (Exception e) {
                log.error("Failed to send email to {}", task.getTo(), e);
            }
        }
    }
}
