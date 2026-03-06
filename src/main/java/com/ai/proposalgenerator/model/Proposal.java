package com.ai.proposalgenerator.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "proposals")
@Data
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_budget")
    private Double totalBudget;
    
    @Column(name = "final_cost")
    private Double finalCost;
    
    @Column(name = "proposal_summary", columnDefinition = "TEXT")
    private String proposalSummary;
}
