package com.example.dash.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {

    private String id;
    private String name;
    private String imageUrl;
    private String imageThumbUrl;
    private String imageMediumUrl;

    private Boolean isActive;
}
