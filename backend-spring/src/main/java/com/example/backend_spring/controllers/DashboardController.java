package com.example.backend_spring.controllers;

import com.example.backend_spring.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/performance")
    public Map<String, Object> getPerformance() {
        return dashboardService.getPerformanceData();
    }

    @GetMapping("/achats")
    public Map<String, Object> getAchatStats() {
        return dashboardService.getAchatDashboardStats();
    }

    @GetMapping("/ventes")
    public Map<String, Object> getVenteStats() {
        return dashboardService.getVenteDashboardStats();
    }
}
