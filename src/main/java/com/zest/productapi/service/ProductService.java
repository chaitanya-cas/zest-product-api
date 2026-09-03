package com.zest.productapi.service;

import com.zest.productapi.dto.ProductRequest;
import com.zest.productapi.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> getAll(Pageable pageable);
    ProductResponse getById(Long id);
    ProductResponse create(ProductRequest request, String username);
    ProductResponse update(Long id, ProductRequest request, String username);
    void delete(Long id);
}
