package com.example.backend_spring.controller.inventaire;

import com.example.backend_spring.service.inventaire.ValorisationStockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/valorisations-stock")
@CrossOrigin(origins = "*")
public class ValorisationStockController {

    private final ValorisationStockService valorisationStockService;

    public ValorisationStockController(ValorisationStockService valorisationStockService) {
        this.valorisationStockService = valorisationStockService;
    }
}
