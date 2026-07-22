package com.example.dash.search.document;

import org.springframework.data.annotation.Id;

import java.time.Instant;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "products")
public class Product {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String currency = "XAF";

    @Field(type = FieldType.Double)
    private Double price = 0.0;

    @Field(type = FieldType.Keyword)
    private String categoryId;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    @Field(type = FieldType.Date)
    private Instant updatedAt;

    @Field(type = FieldType.Boolean)
    private Boolean isActive = true;

    @Field(type = FieldType.Integer)
    private Integer stock = 0;
}