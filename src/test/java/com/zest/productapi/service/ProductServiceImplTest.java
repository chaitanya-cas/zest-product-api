package com.zest.productapi.service;

import com.zest.productapi.dto.ProductRequest;
import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    void createShouldPersistProduct() {
        Product saved = Product.builder()
            .id(1L)
            .productName("Laptop")
            .createdBy("admin")
            .build();

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        var response = productService.create(new ProductRequest("Laptop"), "admin");

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.productName()).isEqualTo("Laptop");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getByIdShouldReturnProduct() {
        Product product = Product.builder()
            .id(1L)
            .productName("Laptop")
            .createdBy("admin")
            .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var response = productService.getById(1L);

        assertThat(response.productName()).isEqualTo("Laptop");
        verify(productRepository).findById(1L);
    }
}
