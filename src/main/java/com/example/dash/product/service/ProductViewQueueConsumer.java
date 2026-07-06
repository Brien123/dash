package com.example.dash.product.service;

import com.example.dash.product.model.ProductViewLog;
import com.example.dash.product.repository.ProductViewLogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductViewQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductViewLogRepository productViewLogRepository;

    private static final String QUEUE_KEY = "product:view:queue";

    @PostConstruct
    public void start() {
        Thread consumer = new Thread(this::consumeLoop, "product-view-consumer");
        consumer.setDaemon(true);
        consumer.start();
    }

    private void consumeLoop() {
        while (true) {
            try {
                ProductViewTask task = (ProductViewTask) redisTemplate
                        .opsForList().leftPop(QUEUE_KEY, 5, TimeUnit.SECONDS);
                while (task != null) {
                    processTask(task);
                    task = (ProductViewTask) redisTemplate
                            .opsForList().leftPop(QUEUE_KEY, 0, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                log.error("Product view consumer error", e);
            }
        }
    }

    private void processTask(ProductViewTask task) {
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
