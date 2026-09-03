package com.zest.productapi.dto;

public record AuthResponse(String accessToken, String refreshToken, String role) {
}
