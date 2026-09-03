package com.zest.productapi.service;

import com.zest.productapi.dto.ItemResponse;
import com.zest.productapi.dto.ProductRequest;
import com.zest.productapi.dto.ProductResponse;
import com.zest.productapi.entity.Product;
import com.zest.productapi.exception.ResourceNotFoundException;
import com.zest.productapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public ProductResponse getById(Long id) {
        return toResponse(find(id));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request, String username) {
        Product product = Product.builder()
            .productName(request.productName())
            .createdBy(username)
            .createdOn(LocalDateTime.now())
            .build();

        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request, String username) {
        Product product = find(id);
        product.setProductName(request.productName());
        product.setModifiedBy(username);
        product.setModifiedOn(LocalDateTime.now());
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Product product = find(id);
        productRepository.delete(product);
    }

    private Product find(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getProductName(),
            product.getCreatedBy(),
            product.getCreatedOn(),
            product.getModifiedBy(),
            product.getModifiedOn(),
            product.getItems().stream()
                .map(item -> new ItemResponse(item.getId(), item.getQuantity()))
                .toList()
        );
    }
}
