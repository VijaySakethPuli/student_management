package com.ai.proposalgenerator.service;

import com.ai.proposalgenerator.dto.AiProposalResponse;
import com.ai.proposalgenerator.model.Product;
import com.ai.proposalgenerator.model.Proposal;
import com.ai.proposalgenerator.repository.ProductRepository;
import com.ai.proposalgenerator.repository.ProposalRepository;
import com.ai.proposalgenerator.repository.AiLogRepository;
import com.ai.proposalgenerator.model.AiLog;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProductRepository productRepository;
    private final ProposalRepository proposalRepository;
    private final AiLogRepository aiLogRepository;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public Proposal generateProposal(Double budget) throws Exception {
        // 1. Fetch available products
        List<Product> products = productRepository.findAll();
        
        String productsListStr = products.stream()
                .map(p -> String.format("ID: %d, Name: %s, Price: $%.2f, Stock: %d, Sustainability Score: %d/10",
                        p.getId(), p.getName(), p.getPricePerUnit(), p.getStockQuantity(), p.getSustainabilityScore()))
                .collect(Collectors.joining("\n"));

        // 2. Construct Prompt
        String systemInstruction = "You are a B2B Sustainability Consultant. Given a list of available products and a budget of $" + budget + ", select a mix of products that maximizes sustainability while staying under budget. Return ONLY a JSON object with this structure: {\"recommended_mix\": [{\"product_id\": 1, \"quantity\": 10, \"cost\": 500}], \"total_allocation\": 4500, \"remaining_budget\": 500, \"impact_summary\": \"This mix reduces plastic waste by 40%...\"}. Do not wrap the JSON in markdown blocks like ```json.";
        
        String promptText = systemInstruction + "\n\nAvailable Products:\n" + productsListStr;

        // 3. Call Gemini API
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Gemini API uses query param for auth: ?key=API_KEY
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

        // Extract text from Gemini response
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
        log.setModuleName("Proposal_Generator_Module2");
        log.setPrompt(promptText);
        log.setResponse(jsonString);
        aiLogRepository.save(log);

        // Parse JSON
        AiProposalResponse aiResponse = objectMapper.readValue(jsonString, AiProposalResponse.class);

        // 4. Math Guard Validation
        double calculatedTotal = 0;
        for (AiProposalResponse.RecommendedItem item : aiResponse.getRecommendedMix()) {
            Long productId = item.getProductId();
            if (productId == null) {
                throw new RuntimeException("AI proposed an item without a product ID.");
            }
            // Find actual product price to verify and calculate
            Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("AI hallucinated product ID: " + productId));
            
            double trueCost = p.getPricePerUnit() * item.getQuantity();
            calculatedTotal += trueCost;
        }

        if (calculatedTotal > budget) {
            throw new RuntimeException("Math Guard Failed: AI suggested an allocation (" + calculatedTotal + ") that exceeds the budget (" + budget + ").");
        }

        // 5. Save Proposal
        Proposal proposal = new Proposal();
        proposal.setTotalBudget(budget);
        proposal.setFinalCost(calculatedTotal);
        proposal.setProposalSummary(jsonString); // Save the raw/parsed JSON
        
        return proposalRepository.save(proposal);
    }
}
