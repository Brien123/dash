package com.example.dash.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDto {
	private String id;
   
	private String imageUrl;
   
    private String imageThumbUrl;

    private String imageMediumUrl;

    private Instant createdAt;

    private Instant updatedAt;
}