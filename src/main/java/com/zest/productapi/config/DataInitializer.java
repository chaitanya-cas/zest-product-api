package com.zest.productapi.config;

import com.zest.productapi.entity.AppUser;
import com.zest.productapi.repository.AppUserRepository;
import com.zest.productapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AppUserRepository userRepository;
    private final AuthService authService;

    @Bean
    CommandLineRunner seedUsers() {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(AppUser.builder()
                    .username("admin")
                    .password(authService.encodePassword("Admin@123"))
                    .role("ADMIN")
                    .build());
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                userRepository.save(AppUser.builder()
                    .username("user")
                    .password(authService.encodePassword("User@123"))
                    .role("USER")
                    .build());
            }
        };
    }
}
