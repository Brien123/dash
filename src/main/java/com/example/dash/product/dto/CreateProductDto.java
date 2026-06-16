package com.example.dash.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {

    private String name;
    private String description;
    private String currency="XAF";
    private Double price;
    private String categoryId;
    private ArrayList<CreateProductImageDto> images;
    private Boolean isActive = true;

    private Integer stock;
}