package com.zest.productapi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductAuditService {

    @Async("taskExecutor")
    public void record(String action, Long productId, String username) {
        log.info("AUDIT action={} productId={} username={}", action, productId, username);
    }
}
