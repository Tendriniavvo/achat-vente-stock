package com.example.backend_spring.controller.stock;

import com.example.backend_spring.service.stock.MouvementStockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mouvements-stock")
@CrossOrigin(origins = "*")
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    public MouvementStockController(MouvementStockService mouvementStockService) {
        this.mouvementStockService = mouvementStockService;
    }
}
