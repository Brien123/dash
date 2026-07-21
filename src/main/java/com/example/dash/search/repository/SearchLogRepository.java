package com.example.dash.search.repository;

import com.example.dash.search.document.SearchLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SearchLogRepository extends MongoRepository<SearchLog, String> {
}
