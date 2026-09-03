package com.zest.productapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequest(
    @NotBlank(message = "productName is required")
    @Size(max = 255, message = "productName must not exceed 255 characters")
    String productName
) {
}
