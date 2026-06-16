package com.example.dash.product.repository;

import com.example.dash.product.model.ProductViewLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductViewLogRepository extends MongoRepository<ProductViewLog, String> {
}
