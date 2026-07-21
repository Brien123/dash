package com.example.dash.search.document;

import org.springframework.data.annotation.Id;

import java.time.Instant;

import org.springframework.data.elasticsearch.annotations.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Mapping;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "products")
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private String currency = "XAF";

    private Double price = 0.0;

    private String categoryId;

    private Instant createdAt;

    private Instant updatedAt;

    private Boolean isActive = true;

    private Integer stock = 0;
}