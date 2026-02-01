package com.example.backend_spring.controllers;

import com.example.backend_spring.services.GlobalAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AIController {

    private final GlobalAIService globalAIService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody Map<String, Object> payload) {
        String message = (String) payload.get("message");
        List<Map<String, String>> history = (List<Map<String, String>>) payload.get("history");
        
        String response = globalAIService.askGlobalQuestion(message, history);
        
        return Map.of("response", response);
    }
}
