package com.zest.productapi.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zest.productapi.dto.AuthRequest;
import com.zest.productapi.dto.AuthResponse;
import com.zest.productapi.dto.RefreshRequest;
import com.zest.productapi.dto.RegisterRequest;
import com.zest.productapi.entity.AppUser;
import com.zest.productapi.entity.RefreshToken;
import com.zest.productapi.repository.AppUserRepository;
import com.zest.productapi.repository.RefreshTokenRepository;
import com.zest.productapi.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final long REFRESH_DAYS = 7;
    
    
    public void register(RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String role = request.role().toUpperCase();

        if (!role.equals("USER") && !role.equals("ADMIN")) {
            throw new IllegalArgumentException("Role must be USER or ADMIN");
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();

        userRepository.save(user);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var user = userRepository.findByUsername(request.username()).orElseThrow();
        String access = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String refresh = generateOpaqueToken();

        refreshTokenRepository.save(RefreshToken.builder()
            .tokenHash(hash(refresh))
            .user(user)
            .expiresAt(LocalDateTime.now().plusDays(REFRESH_DAYS))
            .revoked(false)
            .build());

        return new AuthResponse(access, refresh, user.getRole());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(request.refreshToken()))
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (stored.isRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expired or revoked");
        }

        stored.setRevoked(true);
        var user = stored.getUser();

        String access = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String newRefresh = generateOpaqueToken();

        refreshTokenRepository.save(RefreshToken.builder()
            .tokenHash(hash(newRefresh))
            .user(user)
            .expiresAt(LocalDateTime.now().plusDays(REFRESH_DAYS))
            .revoked(false)
            .build());

        return new AuthResponse(access, newRefresh, user.getRole());
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            return HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not hash token", ex);
        }
    }
}
