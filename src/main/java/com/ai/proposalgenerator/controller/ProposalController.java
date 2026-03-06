package com.ai.proposalgenerator.controller;

import com.ai.proposalgenerator.model.Proposal;
import com.ai.proposalgenerator.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateProposal(@RequestParam Double budget) {
        try {
            Proposal proposal = proposalService.generateProposal(budget);
            return ResponseEntity.ok(proposal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating proposal: " + e.getMessage());
        }
    }
}
