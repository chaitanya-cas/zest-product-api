package com.zest.productapi.controller;

import com.zest.productapi.dto.ProductResponse;
import com.zest.productapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @Test
    @WithMockUser(roles = "USER")
    void getProductsShouldReturnOk() throws Exception {
        when(productService.getAll(any())).thenReturn(
            new PageImpl<>(List.of(
                new ProductResponse(1L, "Laptop", "admin", null, null, null, List.of())
            ), PageRequest.of(0, 10), 1)
        );

        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk());
    }
}
