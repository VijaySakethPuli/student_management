package com.ai.proposalgenerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class AiProposalResponse {
    @JsonProperty("recommended_mix")
    private List<RecommendedItem> recommendedMix;
    
    @JsonProperty("total_allocation")
    private Double totalAllocation;
    
    @JsonProperty("remaining_budget")
    private Double remainingBudget;
    
    @JsonProperty("impact_summary")
    private String impactSummary;

    @Data
    public static class RecommendedItem {
        @JsonProperty("product_id")
        private Long productId;
        
        @JsonProperty("quantity")
        private Integer quantity;
        
        @JsonProperty("cost")
        private Double cost;
    }
}
