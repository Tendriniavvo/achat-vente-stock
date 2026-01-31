package com.example.backend_spring.dto;

import java.util.Map;

public class BudgetDataRequest {
    private Map<String, Object> data;

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
