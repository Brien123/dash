package com.example.dash.product.service.impl;

import com.example.dash.common.file.service.FileStorageService;
import com.example.dash.common.file.service.ImageCompressionHandler;
import com.example.dash.common.file.service.ImageQueueService;
import com.example.dash.product.dto.CreateProductDto;
import com.example.dash.product.dto.CreateProductImageDto;
import com.example.dash.product.dto.ProductDto;
import com.example.dash.product.dto.ProductImageDto;
import com.example.dash.product.mapper.ProductImageMapper;
import com.example.dash.product.mapper.ProductMapper;
import com.example.dash.product.model.Product;
import com.example.dash.product.model.ProductImage;
import com.example.dash.product.repository.ProductImageRepository;
import com.example.dash.product.repository.ProductRepository;
import com.example.dash.product.service.ProductService;
import com.example.dash.search.service.ProductIndexHandler;
import com.example.dash.search.service.ProductIndexQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service("product")
public class ProductServiceImpl implements ProductService, ImageCompressionHandler, ProductIndexHandler {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FileStorageService fileStorageService;
    private final ImageQueueService imageQueueService;
    private final ProductIndexQueueService productIndexQueueService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    @Transactional
    public ProductDto create(CreateProductDto createProductDto) {
        if (createProductDto.getImages() == null || createProductDto.getImages().isEmpty()) {
            throw new IllegalArgumentException("Product must have at least one image.");
        }
        Product product = ProductMapper.toEntity(createProductDto);
        product.setSlug(generateUniqueSlug(createProductDto.getName()));
        product = productRepository.save(product);
        
        List<ProductImage> images = new ArrayList<>();
        for (CreateProductImageDto imageDto : createProductDto.getImages()) {
            String imageUrl = storeImageFile(imageDto);
            
            CreateProductImageDto imageWithProductId = new CreateProductImageDto();
            imageWithProductId.setProductId(product.getId());
            imageWithProductId.setImageUrl(imageUrl);
            
            ProductImage image = ProductImageMapper.toEntity(imageWithProductId);
            image = productImageRepository.save(image);
            
            if (imageUrl != null) {
                imageQueueService.enqueueCompression(imageUrl, "product", image.getId());
            }
            images.add(image);
        }
        
        productIndexQueueService.enqueueIndex(product.getId());
        
        return buildProductDto(product, images);
    }

    @Override
    public ProductDto getById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Product not found with id: " + id));

        List<ProductImage> images = productImageRepository.findByProductId(id);
        return buildProductDto(product, images);
    }

    @Override
    public Page<ProductDto> getAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        List<String> productIds = productPage.getContent().stream()
                .map(Product::getId)
                .toList();

        List<ProductImage> allImages = productImageRepository.findAllByProductIdIn(productIds);

        return productPage.map(product -> {
            List<ProductImage> productImages = allImages.stream()
                    .filter(img -> img.getProductId().equals(product.getId()))
                    .toList();
            return buildProductDto(product, productImages);
        });
    }

    @Override
    public Page<ProductDto> getAllByStatus(Boolean isActive, Pageable pageable) {
        Page<Product> productPage = productRepository.findByIsActive(isActive, pageable);

        List<String> productIds = productPage.getContent().stream()
                .map(Product::getId)
                .toList();

        List<ProductImage> allImages = productImageRepository.findAllByProductIdIn(productIds);

        return productPage.map(product -> {
            List<ProductImage> productImages = allImages.stream()
                    .filter(img -> img.getProductId().equals(product.getId()))
                    .toList();
            return buildProductDto(product, productImages);
        });
    }

    @Override
    @Transactional
    public ProductDto update(String id, CreateProductDto createProductDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Product not found with id: " + id));

        ProductMapper.updateEntity(product, createProductDto);
        product.setSlug(generateUniqueSlug(createProductDto.getName()));
        product = productRepository.save(product);

        List<ProductImage> existingImages = productImageRepository.findByProductId(id);
        for (ProductImage existingImage : existingImages) {
            deleteImageFiles(existingImage);
        }
        productImageRepository.deleteByProductId(id);

        List<ProductImage> images = new ArrayList<>();
        if (createProductDto.getImages() != null) {
            for (CreateProductImageDto imageDto : createProductDto.getImages()) {
                String imageUrl = storeImageFile(imageDto);
                CreateProductImageDto imageWithProductId = new CreateProductImageDto();
                imageWithProductId.setProductId(product.getId());
                imageWithProductId.setImageUrl(imageUrl);
                ProductImage image = ProductImageMapper.toEntity(imageWithProductId);
                image = productImageRepository.save(image);
                if (imageUrl != null) {
                    imageQueueService.enqueueCompression(imageUrl, "product", image.getId());
                }
                images.add(image);
            }
        }

        productIndexQueueService.enqueueIndex(product.getId());

        return buildProductDto(product, images);
    }

    @Override
    @Transactional
    public void delete(String id) {
        List<ProductImage> images = productImageRepository.findByProductId(id);
        for (ProductImage image : images) {
            deleteImageFiles(image);
        }

        productImageRepository.deleteByProductId(id);
        productRepository.deleteById(id);

        productIndexQueueService.enqueueDelete(id);
    }

    @Override
    public Page<ProductDto> getProductByCategory(String categoryId, Pageable pageable){
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        List<String> productIds = productPage.getContent().stream()
                .map(Product::getId)
                .toList();

        List<ProductImage> allImages = productImageRepository.findAllByProductIdIn(productIds);

        return productPage.map(product -> {
            List<ProductImage> productImages = allImages.stream()
                    .filter(img -> img.getProductId().equals(product.getId()))
                    .toList();
            return buildProductDto(product, productImages);
        });
    }

    @Override
    public void onCompressionComplete(String entityId, String imageUrl, String thumbUrl, String mediumUrl) {
        productImageRepository.findById(entityId).ifPresent(image -> {
            image.setImageThumbUrl(thumbUrl);
            image.setImageMediumUrl(mediumUrl);
            productImageRepository.save(image);
        });
    }

    @Override
    public void onIndexComplete(String productId) {
    }

    @Override
    public void onDeleteComplete(String productId) {
    }

    private String storeImageFile(CreateProductImageDto imageDto) {
        if (imageDto.getFile() != null && !imageDto.getFile().isEmpty()) {
            return fileStorageService.store(imageDto.getFile(), "products");
        }
        return imageDto.getImageUrl();
    }

    private void deleteImageFiles(ProductImage image) {
        if (image.getImageUrl() != null) {
            fileStorageService.delete(image.getImageUrl());
        }
        if (image.getImageThumbUrl() != null) {
            fileStorageService.delete(image.getImageThumbUrl());
        }
        if (image.getImageMediumUrl() != null) {
            fileStorageService.delete(image.getImageMediumUrl());
        }
    }

    private ProductDto buildProductDto(Product product, List<ProductImage> images) {
        ProductDto dto = ProductMapper.toDto(product);
        List<ProductImageDto> imageDtos = images.stream()
                .map(ProductImageMapper::toDto)
                .map(this::resolveImageUrls)
                .toList();
        dto.setImages(new ArrayList<>(imageDtos));
        return dto;
    }

    private ProductImageDto resolveImageUrls(ProductImageDto dto) {
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

    private String generateUniqueSlug(String name) {
        String base = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        String slug = base;
        int suffix = 1;
        while (productRepository.existsBySlug(slug)) {
            slug = base + "-" + suffix++;
        }
        return slug;
    }
}
