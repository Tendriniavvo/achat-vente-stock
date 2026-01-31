package com.example.backend_spring.dto;

import java.util.List;
import java.util.Map;

public class ChatRequest {
    private String message;
    private Map<String, Object> budgetData;
    private List<Map<String, String>> history;

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, Object> getBudgetData() { return budgetData; }
    public void setBudgetData(Map<String, Object> budgetData) { this.budgetData = budgetData; }

    public List<Map<String, String>> getHistory() { return history; }
    public void setHistory(List<Map<String, String>> history) { this.history = history; }
}
