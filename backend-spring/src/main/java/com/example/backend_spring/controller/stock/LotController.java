package com.example.backend_spring.controller.stock;

import com.example.backend_spring.service.stock.LotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lots")
@CrossOrigin(origins = "*")
public class LotController {

    private final LotService lotService;

    public LotController(LotService lotService) {
        this.lotService = lotService;
    }
}
