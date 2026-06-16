package com.example.dash.product.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.dash.product.model.ProductImage;

public interface ProductImageRepository extends MongoRepository<ProductImage, String>{
    ArrayList<ProductImage> findByProductId(String productId);

    List<ProductImage> findAllByProductIdIn(List<String> productIds);

    void deleteByProductId(String productId);
}
