package com.example.dash.search.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.dash.search.document.Product;

public interface ProductSearchRepository extends ElasticsearchRepository<Product, String>{
    Page<Product> findByName(String name, Pageable pageable);
}