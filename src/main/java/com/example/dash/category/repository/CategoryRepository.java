package com.example.dash.category.repository;

import com.example.dash.category.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryRepository extends MongoRepository<Category, String> {
    Page<Category> findByIsActive(Boolean isActive, Pageable pageable);
}
