package com.example.dash.category.service;

import com.example.dash.category.dto.CategoryDto;
import com.example.dash.category.dto.UpdateCategoryDto;
import com.example.dash.category.dto.CreateCategoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryDto create(CreateCategoryDto categoryCreateDto);
    CategoryDto update(String id, UpdateCategoryDto categoryUpdateDto);
    CategoryDto getById(String id);
    Page<CategoryDto> getAll(Pageable pageable);
    Page<CategoryDto> getAllByStatus(Boolean isActive, Pageable pageable);
    void delete(String id);
}
