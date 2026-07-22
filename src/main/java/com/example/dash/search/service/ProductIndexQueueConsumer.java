package com.example.dash.search.service;

import com.example.dash.product.model.Product;
import com.example.dash.product.repository.ProductRepository;
import com.example.dash.search.document.SearchLog;
import com.example.dash.search.dto.SearchRequestDto;
import com.example.dash.search.repository.SearchLogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIndexQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    private final SearchLogRepository searchLogRepository;
    private final com.example.dash.search.repository.ProductSearchRepository searchProductRepository;
    private final Map<String, ProductIndexHandler> indexHandlers;

    private static final String QUEUE_KEY = "product:index:queue";
    private static final String SEARCH_LOG_QUEUE_KEY = "search:log:queue";

    @PostConstruct
    public void start() {
        Thread indexConsumer = new Thread(this::consumeIndexLoop, "product-index-consumer");
        indexConsumer.setDaemon(true);
        indexConsumer.start();

        Thread searchLogConsumer = new Thread(this::consumeSearchLogLoop, "search-log-consumer");
        searchLogConsumer.setDaemon(true);
        searchLogConsumer.start();
    }

    private void consumeIndexLoop() {
        while (true) {
            try {
                ProductIndexTask task = (ProductIndexTask) redisTemplate
                        .opsForList().leftPop(QUEUE_KEY, 5, TimeUnit.SECONDS);
                while (task != null) {
                    processTask(task);
                    task = (ProductIndexTask) redisTemplate
                            .opsForList().leftPop(QUEUE_KEY, 0, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                log.error("Product index consumer error", e);
            }
        }
    }

    private void consumeSearchLogLoop() {
        while (true) {
            try {
                ProductSearchTask task = (ProductSearchTask) redisTemplate
                        .opsForList().leftPop(SEARCH_LOG_QUEUE_KEY, 5, TimeUnit.SECONDS);
                while (task != null) {
                    processSearch(task);
                    task = (ProductSearchTask) redisTemplate
                            .opsForList().leftPop(SEARCH_LOG_QUEUE_KEY, 0, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                log.error("Search log consumer error", e);
            }
        }
    }

    private void processTask(ProductIndexTask task) {
        try {
            switch (task.getType()) {
                case "index" -> processIndex(task);
                case "delete" -> processDelete(task);
                default -> log.warn("Unknown product index task type: {}", task.getType());
            }
        } catch (Exception e) {
            log.error("Failed to process product index task for {}", task.getProductId(), e);
        }
    }

    private void processIndex(ProductIndexTask task) {
        Product product = productRepository.findById(task.getProductId()).orElse(null);
        if (product == null) {
            log.warn("Product not found for indexing: {}", task.getProductId());
            return;
        }
        com.example.dash.search.document.Product searchProduct = mapToSearchProduct(product);
        searchProductRepository.save(searchProduct);
        log.info("Indexed product in Elasticsearch: {}", task.getProductId());

        ProductIndexHandler handler = indexHandlers.get("product");
        if (handler != null) {
            handler.onIndexComplete(task.getProductId());
        }
    }

    private void processDelete(ProductIndexTask task) {
        searchProductRepository.deleteById(task.getProductId());
        log.info("Deleted product from Elasticsearch: {}", task.getProductId());

        ProductIndexHandler handler = indexHandlers.get("product");
        if (handler != null) {
            handler.onDeleteComplete(task.getProductId());
        }
    }

    private void processSearch(ProductSearchTask task){
        SearchLog searchLog = new SearchLog();
        searchLog.setUserId(task.getUserId());
        searchLog.setSearchRequest(task.getSearchRequestDto());
        searchLog.setNumberOfResults(task.getNumberOfResults());
        searchLogRepository.save(searchLog);
    }

    public com.example.dash.search.document.Product mapToSearchProduct(Product product) {
        com.example.dash.search.document.Product searchProduct = new com.example.dash.search.document.Product();
        searchProduct.setId(product.getId());
        searchProduct.setName(product.getName());
        searchProduct.setDescription(product.getDescription());
        searchProduct.setCurrency(product.getCurrency());
        searchProduct.setPrice(product.getPrice());
        searchProduct.setCategoryId(product.getCategoryId());
        searchProduct.setCreatedAt(product.getCreatedAt());
        searchProduct.setUpdatedAt(product.getUpdatedAt());
        searchProduct.setIsActive(product.getIsActive());
        searchProduct.setStock(product.getStock());
        return searchProduct;
    }
}