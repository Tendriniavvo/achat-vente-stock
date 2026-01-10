package com.example.backend_spring.controller.stock;

import com.example.backend_spring.service.stock.DepotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/depots")
@CrossOrigin(origins = "*")
public class DepotController {

    private final DepotService depotService;

    public DepotController(DepotService depotService) {
        this.depotService = depotService;
    }
}
