package com.example.dash.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.dash.product.dto.ProductDto;
import com.example.dash.product.mapper.ProductMapper;
import com.example.dash.product.model.Product;
import com.example.dash.product.repository.ProductRepository;
import com.example.dash.search.dto.SearchRequestDto;
import com.example.dash.search.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductSearch {
    private final ProductSearchRepository productSearchRepository;
    private final ProductRepository productRepository;
    private final ProductIndexQueueConsumer productIndexQueueConsumer;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductIndexQueueService productIndexQueueService;

    public void bulkIndexProducts(){
        List<Product> products = productRepository.findByIsActive(true);
        List<com.example.dash.search.document.Product> productDocuments = products.stream()
                .map(productIndexQueueConsumer::mapToSearchProduct).toList();
        productSearchRepository.saveAll(productDocuments);
    }

    public Page<ProductDto> search(SearchRequestDto request, Long userId, Pageable pageable) {

        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

        if (StringUtils.hasText(request.getQuery())) {
            Query mainSearchQuery = QueryBuilders.multiMatch(m -> m
                    .query(request.getQuery())
                    .fields("name^3.0", "description^1.0")
                    .fuzziness("AUTO")
            );
            boolQueryBuilder.must(mainSearchQuery);
        }

        // Category Filter
        if (StringUtils.hasText(request.getCategoryId())) {
            boolQueryBuilder.filter(f -> f
                    .term(t -> t.field("categoryId").value(request.getCategoryId()))
            );
        }

        // Price Range Filter
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            boolQueryBuilder.filter(f -> f
                    .range(r -> {
                        r.field("price");
                        if (request.getMinPrice() != null) r.gte(co.elastic.clients.json.JsonData.of(request.getMinPrice()));
                        if (request.getMaxPrice() != null) r.lte(co.elastic.clients.json.JsonData.of(request.getMaxPrice()));
                        return r;
                    })
            );
        }

        // Native Query Builder
        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(boolQueryBuilder.build()._toQuery())
                .withPageable(pageable);

        // Custom Sorting
        if (StringUtils.hasText(request.getSortBy())) {
            switch (request.getSortBy().toLowerCase()) {
                case "price_asc":
                    nativeQueryBuilder.withSort(Sort.by(Sort.Direction.ASC, "price"));
                    break;
                case "price_desc":
                    nativeQueryBuilder.withSort(Sort.by(Sort.Direction.DESC, "price"));
                    break;
                case "newest":
                    nativeQueryBuilder.withSort(Sort.by(Sort.Direction.DESC, "createdAt"));
                    break;
                default:
                    nativeQueryBuilder.withSort(Sort.by(Sort.Direction.DESC, "_score"));
                    break;
            }
        }

        // Search against Elasticsearch
        SearchHits<com.example.dash.search.document.Product> searchHits = elasticsearchOperations.search(
                nativeQueryBuilder.build(),
                com.example.dash.search.document.Product.class
        );

        List<ProductDto> dtos = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(this::maptoProduct)
                .map(ProductMapper::toDto)
                .toList();

        productIndexQueueService.enqueueSearch(request, userId, searchHits.getTotalHits());
        return new PageImpl<>(dtos, pageable, searchHits.getTotalHits());
    }

    private Product maptoProduct(com.example.dash.search.document.Product product){
        return productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}