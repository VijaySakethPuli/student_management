package com.ai.proposalgenerator.repository;

import com.ai.proposalgenerator.model.AiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AiLogRepository extends JpaRepository<AiLog, Long> {
    List<AiLog> findAllByOrderByTimestampDesc();
}
