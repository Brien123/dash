package com.example.dash.search.service;

import com.example.dash.search.dto.SearchRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchTask {
    private Long userId;
    private SearchRequestDto searchRequestDto;
    private Long numberOfResults;
    private String type;
}
