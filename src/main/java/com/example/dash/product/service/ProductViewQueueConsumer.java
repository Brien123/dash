package com.example.dash.product.service;

import com.example.dash.product.model.ProductViewLog;
import com.example.dash.product.repository.ProductViewLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductViewQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductViewLogRepository productViewLogRepository;

    private static final String QUEUE_KEY = "product:view:queue";

    @Scheduled(fixedDelay = 1000)
    public void processQueue() {
        ProductViewTask task = (ProductViewTask) redisTemplate.opsForList().leftPop(QUEUE_KEY);
        if (task != null) {
            try {
                ProductViewLog logEntry = new ProductViewLog();
                logEntry.setUserId(task.getUserId());
                logEntry.setProductId(task.getProductId());
                productViewLogRepository.save(logEntry);
                log.info("Logged product view: userId={}, productId={}", task.getUserId(), task.getProductId());
            } catch (Exception e) {
                log.error("Failed to process product view task for product {}", task.getProductId(), e);
            }
        }
    }
}
