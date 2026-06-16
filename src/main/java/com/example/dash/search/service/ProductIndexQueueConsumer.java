package com.example.dash.search.service;

import com.example.dash.product.model.Product;
import com.example.dash.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIndexQueueConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    private final com.example.dash.search.repository.ProductSearchRepository searchProductRepository;
    private final Map<String, ProductIndexHandler> indexHandlers;

    private static final String QUEUE_KEY = "product:index:queue";

    @Scheduled(fixedDelay = 1000)
    public void processQueue() {
        ProductIndexTask task = (ProductIndexTask) redisTemplate.opsForList().leftPop(QUEUE_KEY);
        if (task != null) {
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

    private com.example.dash.search.document.Product mapToSearchProduct(Product product) {
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