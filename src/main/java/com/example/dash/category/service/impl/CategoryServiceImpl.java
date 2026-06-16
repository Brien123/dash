package com.example.dash.category.service.impl;

import com.example.dash.category.dto.CategoryDto;
import com.example.dash.category.dto.CreateCategoryDto;
import com.example.dash.category.dto.UpdateCategoryDto;
import com.example.dash.category.mapper.CategoryMapper;
import com.example.dash.category.model.Category;
import com.example.dash.category.repository.CategoryRepository;
import com.example.dash.category.service.CategoryService;
import com.example.dash.common.file.service.FileStorageService;
import com.example.dash.common.file.service.ImageCompressionHandler;
import com.example.dash.common.file.service.ImageQueueService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("category")
public class CategoryServiceImpl implements CategoryService, ImageCompressionHandler {

    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final ImageQueueService imageQueueService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public CategoryDto create(CreateCategoryDto createCategoryDto) {
        Category category = CategoryMapper.toEntity(createCategoryDto);

        String imageUrl = null;
        if (createCategoryDto.getImage() != null && !createCategoryDto.getImage().isEmpty()) {
            imageUrl = fileStorageService.store(createCategoryDto.getImage(), "categories");
            category.setImageUrl(imageUrl);
        }

        Category savedCategory = categoryRepository.save(category);

        if (imageUrl != null) {
            imageQueueService.enqueueCompression(imageUrl, "category", savedCategory.getId());
        }

        return resolveImageUrls(CategoryMapper.toDto(savedCategory));
    }

    @Override
    public CategoryDto update(String id, UpdateCategoryDto categoryUpdateDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        Category updatedCategory = CategoryMapper.updateEntity(existingCategory, categoryUpdateDto);

        String imageUrl = null;
        if (categoryUpdateDto.getImage() != null && !categoryUpdateDto.getImage().isEmpty()) {
            if (existingCategory.getImageUrl() != null) {
                fileStorageService.delete(existingCategory.getImageUrl());
            }
            imageUrl = fileStorageService.store(categoryUpdateDto.getImage(), "categories");
            updatedCategory.setImageUrl(imageUrl);
        }

        Category savedCategory = categoryRepository.save(updatedCategory);

        if (imageUrl != null) {
            imageQueueService.enqueueCompression(imageUrl, "category", savedCategory.getId());
        }

        return resolveImageUrls(CategoryMapper.toDto(savedCategory));
    }

    @Override
    public void onCompressionComplete(String entityId, String imageUrl, String thumbUrl, String mediumUrl) {
        categoryRepository.findById(entityId).ifPresent(category -> {
            category.setImageThumbUrl(thumbUrl);
            category.setImageMediumUrl(mediumUrl);
            categoryRepository.save(category);
        });
    }

    @Override
    public CategoryDto getById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return resolveImageUrls(CategoryMapper.toDto(category));
    }

    @Override
    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(category -> resolveImageUrls(CategoryMapper.toDto(category)));
    }

    @Override
    public Page<CategoryDto> getAllByStatus(Boolean isActive, Pageable pageable) {
        return categoryRepository.findByIsActive(isActive, pageable)
                .map(category -> resolveImageUrls(CategoryMapper.toDto(category)));
    }

    private CategoryDto resolveImageUrls(CategoryDto dto) {
        dto.setImageUrl(resolveUrl(dto.getImageUrl()));
        dto.setImageThumbUrl(resolveUrl(dto.getImageThumbUrl()));
        dto.setImageMediumUrl(resolveUrl(dto.getImageMediumUrl()));
        return dto;
    }

    private String resolveUrl(String url) {
        if (url == null || url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        return baseUrl + "/uploads/" + url;
    }

    @Override
    public void delete(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        if (category.getImageUrl() != null) {
            fileStorageService.delete(category.getImageUrl());
        }

        categoryRepository.deleteById(id);
    }
}
