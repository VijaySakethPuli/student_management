package com.ai.proposalgenerator.controller;

import com.ai.proposalgenerator.dto.ProductCategorizationResponse;
import com.ai.proposalgenerator.model.Product;
import com.ai.proposalgenerator.service.ProductOnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductOnboardingService productOnboardingService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeDescription(@RequestBody String messyDescription) {
        try {
            ProductCategorizationResponse response = productOnboardingService.analyzeProductDescription(messyDescription);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error analyzing product: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveProduct(@RequestBody @org.springframework.lang.NonNull Product product) {
        try {
            Product savedProduct = productOnboardingService.saveProduct(product);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving product: " + e.getMessage());
        }
    }
}
