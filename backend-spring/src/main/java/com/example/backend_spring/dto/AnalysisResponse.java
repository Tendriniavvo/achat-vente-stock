package com.example.backend_spring.dto;

public class AnalysisResponse {
    private String analysis;
    private String status;

    public AnalysisResponse() {
    }

    public AnalysisResponse(String analysis, String status) {
        this.analysis = analysis;
        this.status = status;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
