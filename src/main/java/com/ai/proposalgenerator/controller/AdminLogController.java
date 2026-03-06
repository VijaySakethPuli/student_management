package com.ai.proposalgenerator.controller;

import com.ai.proposalgenerator.model.AiLog;
import com.ai.proposalgenerator.repository.AiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final AiLogRepository aiLogRepository;

    @GetMapping
    public ResponseEntity<List<AiLog>> getAllLogs() {
        return ResponseEntity.ok(aiLogRepository.findAllByOrderByTimestampDesc());
    }
}
