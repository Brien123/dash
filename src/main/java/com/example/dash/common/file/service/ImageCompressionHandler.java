package com.example.dash.common.file.service;

public interface ImageCompressionHandler {
    void onCompressionComplete(String entityId, String imageUrl, String thumbUrl, String mediumUrl);
}
