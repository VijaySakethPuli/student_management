package com.ai.proposalgenerator.service;

import com.ai.proposalgenerator.dto.ProductCategorizationResponse;
import com.ai.proposalgenerator.model.AiLog;
import com.ai.proposalgenerator.model.Product;
import com.ai.proposalgenerator.repository.AiLogRepository;
import com.ai.proposalgenerator.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductOnboardingService {

    private final ProductRepository productRepository;
    private final AiLogRepository aiLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public ProductCategorizationResponse analyzeProductDescription(String messyDescription) throws Exception {
        String systemInstruction = "You are an AI Sustainability Product Categorizer. Extract information from the messy product description provided. " +
                "Categorize the product, generate 3-5 SEO tags, apply relevant sustainability badges (e.g., 'Vegan', 'Plastic-Free', 'Recycled', 'Carbon-Neutral'), " +
                "and infer a reasonable B2B bulk price per unit (e.g., 45.00) based on context. " +
                "Return ONLY a JSON object with this exact structure: {\"category\": \"Office Supplies\", \"seo_tags\": [\"recycled\", \"paper\"], " +
                "\"sustainability_badges\": [\"Plastic-Free\", \"Recycled\"], \"inferred_price\": 10.50}. Do not wrap in markdown.";

        String promptText = systemInstruction + "\n\nMessy Description: " + messyDescription;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String urlWithKey = apiUrl + "?key=" + apiKey;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", promptText)
            ))
        ));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(urlWithKey, entity, Map.class);
        
        if (response == null) {
            throw new RuntimeException("No response from AI");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No response from AI");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        String jsonString = (String) parts.get(0).get("text");

        // Clean markdown if present
        jsonString = jsonString.trim();
        if (jsonString.startsWith("```json")) {
            jsonString = jsonString.substring(7);
        }
        if (jsonString.startsWith("```")) {
            jsonString = jsonString.substring(3);
        }
        if (jsonString.endsWith("```")) {
            jsonString = jsonString.substring(0, jsonString.length() - 3);
        }

        // Save AI Log
        AiLog log = new AiLog();
        log.setModuleName("Product_Onboarding_Module1");
        log.setPrompt(promptText);
        log.setResponse(jsonString);
        aiLogRepository.save(log);

        return objectMapper.readValue(jsonString, ProductCategorizationResponse.class);
    }

    public Product saveProduct(@org.springframework.lang.NonNull Product product) {
        return productRepository.save(product);
    }
}
