package com.example.dash.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
    String query;
    Integer minPrice;
    Integer maxPrice;
    String categoryId;
    String sortBy;
}
