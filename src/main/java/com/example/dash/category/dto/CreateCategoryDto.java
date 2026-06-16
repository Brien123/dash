package com.example.dash.category.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateCategoryDto {
    private String name;
    private MultipartFile image;
    private Boolean isActive = true;
}
