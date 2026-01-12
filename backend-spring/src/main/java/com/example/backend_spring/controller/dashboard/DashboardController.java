package com.example.backend_spring.controller.dashboard;

import com.example.backend_spring.dto.dashboard.DashboardAchatsResponse;
import com.example.backend_spring.dto.dashboard.DashboardKpisResponse;
import com.example.backend_spring.service.dashboard.DashboardAchatsService;
import com.example.backend_spring.service.dashboard.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardAchatsService dashboardAchatsService;

    public DashboardController(DashboardService dashboardService, DashboardAchatsService dashboardAchatsService) {
        this.dashboardService = dashboardService;
        this.dashboardAchatsService = dashboardAchatsService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<DashboardKpisResponse> getKpis() {
        return ResponseEntity.ok(dashboardService.getKpis());
    }

    @GetMapping("/achats")
    public ResponseEntity<DashboardAchatsResponse> getAchats(@RequestParam(value = "periode", required = false) String periode) {
        return ResponseEntity.ok(dashboardAchatsService.getAchats(periode));
    }
}
