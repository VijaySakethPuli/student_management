package com.ai.proposalgenerator.repository;

import com.ai.proposalgenerator.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
