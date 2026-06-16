package com.example.dash.search.service;

public interface ProductIndexHandler {
    void onIndexComplete(String productId);
    void onDeleteComplete(String productId);
}