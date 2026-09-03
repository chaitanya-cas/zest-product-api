package com.zest.productapi;

import com.zest.productapi.entity.Product;
import com.zest.productapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryIntegrationTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void shouldPersistAndReadProduct() {
        Product product = Product.builder()
            .productName("Integration Product")
            .createdBy("test")
            .createdOn(LocalDateTime.now())
            .build();

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(productRepository.findById(saved.getId())).isPresent();
    }
}
