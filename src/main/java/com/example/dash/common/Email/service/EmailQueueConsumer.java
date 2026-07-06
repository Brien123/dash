package com.example.dash.common.Email.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;

    private static final String QUEUE_KEY = "email:queue";

    @PostConstruct
    public void start() {
        Thread consumer = new Thread(this::consumeLoop, "email-consumer");
        consumer.setDaemon(true);
        consumer.start();
    }

    private void consumeLoop() {
        while (true) {
            try {
                EmailTask task = (EmailTask) redisTemplate
                        .opsForList().leftPop(QUEUE_KEY, 5, TimeUnit.SECONDS);
                while (task != null) {
                    processTask(task);
                    task = (EmailTask) redisTemplate
                            .opsForList().leftPop(QUEUE_KEY, 0, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                log.error("Consumer error", e);
            }
        }
    }

    private void processTask(EmailTask task) {
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
