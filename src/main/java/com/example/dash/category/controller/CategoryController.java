package com.example.dash.category.controller;

import com.example.dash.category.dto.CategoryDto;
import com.example.dash.category.dto.CreateCategoryDto;
import com.example.dash.category.dto.UpdateCategoryDto;
import com.example.dash.category.service.CategoryService;
import com.example.dash.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/category/v1")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Page<CategoryDto>>> getAll(
            @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) Boolean isActive
    ){
        if (isActive != null) {
            return ResponseEntity.ok(ApiResponse.success("Categories loaded successfully", categoryService.getAllByStatus(isActive, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.success("Categories loaded successfully", categoryService.getAll(pageable) ));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryDto>> getById(@PathVariable String categoryId){
        return ResponseEntity.ok(ApiResponse.success("Category loaded successfully", categoryService.getById(categoryId) ));
    }

    @PatchMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> update(@PathVariable String categoryId, @ModelAttribute UpdateCategoryDto updateCategoryDto){
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", categoryService.update(categoryId, updateCategoryDto) ));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> create(@ModelAttribute CreateCategoryDto createCategoryDto){
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", categoryService.create(createCategoryDto) ));
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String categoryId){
        categoryService.delete(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }
}
