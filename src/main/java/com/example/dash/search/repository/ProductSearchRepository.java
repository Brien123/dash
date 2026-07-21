package com.example.dash.search.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.dash.search.document.Product;

import java.time.Instant;

public interface ProductSearchRepository extends ElasticsearchRepository<Product, String> {
    Page<Product> findByName(String name, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}], \"filter\": [{\"range\": {\"price\": {\"gte\": ?1, \"lte\": ?2}}}, {\"term\": {\"categoryId\": \"?3\"}}, {\"term\": {\"isActive\": true}}]}}")
    Page<Product> findByNameAndPriceRangeAndCategory(String name, Double minPrice, Double maxPrice, String categoryId, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}], \"filter\": [{\"range\": {\"price\": {\"gte\": ?1, \"lte\": ?2}}}, {\"term\": {\"isActive\": true}}]}}")
    Page<Product> findByNameAndPriceRange(String name, Double minPrice, Double maxPrice, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}], \"filter\": [{\"term\": {\"categoryId\": \"?1\"}}, {\"term\": {\"isActive\": true}}]}}")
    Page<Product> findByNameAndCategory(String name, String categoryId, Pageable pageable);

    @Query("{\"bool\": {\"filter\": [{\"range\": {\"price\": {\"gte\": ?0, \"lte\": ?1}}}, {\"term\": {\"categoryId\": \"?2\"}}, {\"term\": {\"isActive\": true}}]}}")
    Page<Product> findByPriceRangeAndCategory(Double minPrice, Double maxPrice, String categoryId, Pageable pageable);

    @Query("{\"bool\": {\"filter\": [{\"range\": {\"price\": {\"gte\": ?0, \"lte\": ?1}}}, {\"term\": {\"isActive\": true}}]}}")
    Page<Product> findByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);

    @Query("{\"bool\": {\"filter\": [{\"term\": {\"categoryId\": \"?0\"}}, {\"term\": {\"isActive\": true}}]}}")
    Page<Product> findByCategoryId(String categoryId, Pageable pageable);

    @Query("{\"bool\": {\"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<Product> findAllActive(Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name^3\", \"description\"]}}], \"filter\": [{\"term\": {\"isActive\": true}}]}}")
    Page<Product> searchByQuery(String query, Pageable pageable);
}