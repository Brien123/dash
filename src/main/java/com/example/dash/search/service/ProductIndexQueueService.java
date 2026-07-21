package com.example.dash.search.service;

import com.example.dash.search.dto.SearchRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIndexQueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "product:index:queue";
    private static final String SEARCH_LOG_QUEUE_KEY = "search:log:queue";
    private static final long MAX_QUEUE_SIZE = 1000;

    public boolean enqueueIndex(String productId) {
        if (!hasRoom()) return false;
        ProductIndexTask task = new ProductIndexTask(productId, "index");
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
        return true;
    }

    public boolean enqueueDelete(String productId) {
        if (!hasRoom()) return false;
        ProductIndexTask task = new ProductIndexTask(productId, "delete");
        redisTemplate.opsForList().rightPush(QUEUE_KEY, task);
        return true;
    }

    private boolean hasRoom() {
        Long size = redisTemplate.opsForList().size(QUEUE_KEY);
        if (size != null && size >= MAX_QUEUE_SIZE) {
            log.warn("Product index queue is full ({} items). Dropping task.", size);
            return false;
        }
        return true;
    }

    public boolean enqueueSearch(SearchRequestDto searchRequestDto, Long userId, Long numberOfResults){
        ProductSearchTask productSearchTask = new ProductSearchTask();
        productSearchTask.setSearchRequestDto(searchRequestDto);
        productSearchTask.setType("search");
        productSearchTask.setUserId(userId);
        productSearchTask.setNumberOfResults(numberOfResults);
        redisTemplate.opsForList().rightPush(SEARCH_LOG_QUEUE_KEY, productSearchTask);
        return true;
    }
}