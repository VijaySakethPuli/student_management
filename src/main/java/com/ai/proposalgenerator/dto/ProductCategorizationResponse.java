package com.ai.proposalgenerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ProductCategorizationResponse {
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("seo_tags")
    private List<String> seoTags;
    
    @JsonProperty("sustainability_badges")
    private List<String> sustainabilityBadges;

    @JsonProperty("inferred_price")
    private Double inferredPrice;
}
