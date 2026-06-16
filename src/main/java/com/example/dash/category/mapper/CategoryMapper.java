package com.example.dash.category.mapper;

import com.example.dash.category.dto.CategoryDto;
import com.example.dash.category.dto.CreateCategoryDto;
import com.example.dash.category.dto.UpdateCategoryDto;
import com.example.dash.category.model.Category;

public class CategoryMapper {

    public static CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setImageUrl(category.getImageUrl());
        categoryDto.setImageThumbUrl(category.getImageThumbUrl());
        categoryDto.setImageMediumUrl(category.getImageMediumUrl());
        categoryDto.setIsActive(category.getIsActive());

        return categoryDto;
    }

    public static Category toEntity(CreateCategoryDto createDto) {
        Category category = new Category();
        category.setName(createDto.getName());
        category.setIsActive(createDto.getIsActive());

        return category;
    }

    public static Category updateEntity(
        Category entity,
        UpdateCategoryDto updateDto
    ) {
        if (updateDto.getName() != null) {
            entity.setName(updateDto.getName());
        }
        if (updateDto.getIsActive() != null) {
            entity.setIsActive(updateDto.getIsActive());
        }

        return entity;
    }
}
