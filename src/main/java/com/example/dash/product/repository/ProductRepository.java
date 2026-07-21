package com.example.dash.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.dash.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String>{
     Page<Product> findByCategoryId(String categoryId, Pageable pageable);
     Page<Product> findByIsActive(Boolean isActive, Pageable pageable);
     Optional<Product> findBySlug(String slug);
     boolean existsBySlug(String slug);
     List<Product> findByIsActive(Boolean isActive);
 }