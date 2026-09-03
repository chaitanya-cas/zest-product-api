package com.zest.productapi.controller;

import com.zest.productapi.dto.ItemRequest;
import com.zest.productapi.dto.ItemResponse;
import com.zest.productapi.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    private final ItemService itemService;

    // ADD ITEM
    @PostMapping
    @Operation(summary = "Add item to a product")
    public ResponseEntity<ItemResponse> addItem(
            @PathVariable Long productId,
            @Valid @RequestBody ItemRequest request) {

        ItemResponse response =
                itemService.addItem(productId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ITEMS
    @GetMapping
    @Operation(summary = "Get all items for a product")
    public ResponseEntity<List<ItemResponse>> getItems(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                itemService.getByProductId(productId)
        );
    }
}