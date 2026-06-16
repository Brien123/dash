package com.example.dash.cart.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cart_item")
public class CartItem {

	@Id
	private String id;

	@Indexed
	private String cartId;

	private String productId;

	private Long quantity;

	private Instant createdAt;

	private Instant updatedAt;
}