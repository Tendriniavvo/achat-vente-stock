package com.example.backend_spring.controller.inventaire;

import com.example.backend_spring.service.inventaire.AjustementStockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ajustements-stock")
@CrossOrigin(origins = "*")
public class AjustementStockController {

    private final AjustementStockService ajustementStockService;

    public AjustementStockController(AjustementStockService ajustementStockService) {
        this.ajustementStockService = ajustementStockService;
    }
}
