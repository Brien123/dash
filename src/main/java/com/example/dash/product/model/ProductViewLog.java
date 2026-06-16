package com.example.dash.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product_view_logs")
public class ProductViewLog {

    @Id
    private String id;

    private Long userId;

    private String productId;

    @CreatedDate
    private Instant viewedAt;
}
