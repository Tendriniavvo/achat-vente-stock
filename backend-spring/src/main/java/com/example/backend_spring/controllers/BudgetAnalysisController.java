package com.example.backend_spring.controllers;

import com.example.backend_spring.dto.AnalysisResponse;
import com.example.backend_spring.dto.BudgetDataRequest;
import com.example.backend_spring.dto.ChatRequest;
import com.example.backend_spring.services.MistralAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:8081", "http://localhost:5173"})
public class BudgetAnalysisController {

    @Autowired
    private MistralAIService mistralAIService;

    @PostMapping("/analyze-chart")
    public ResponseEntity<AnalysisResponse> analyzeChart(@RequestBody BudgetDataRequest request) {
        try {
            String analysis = mistralAIService.analyzerGraphiqueBudget(request.getData());
            return ResponseEntity.ok(new AnalysisResponse(analysis, "success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AnalysisResponse("Erreur lors de l'analyse : " + e.getMessage(), "error"));
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<AnalysisResponse> chat(@RequestBody ChatRequest request) {
        try {
            String response = mistralAIService.chatWithAI(request.getMessage(), request.getBudgetData(), request.getHistory());
            return ResponseEntity.ok(new AnalysisResponse(response, "success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AnalysisResponse("Erreur lors de la discussion : " + e.getMessage(), "error"));
        }
    }
}
