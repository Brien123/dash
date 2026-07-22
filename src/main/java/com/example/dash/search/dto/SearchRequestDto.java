package com.example.dash.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {

    @Field("query")
    private String query;

    @Field("minPrice")
    private Double minPrice;

    @Field("maxPrice")
    private Double maxPrice;

    @Field("categoryId")
    private String categoryId;

    @Field("sortBy")
    private String sortBy;
}