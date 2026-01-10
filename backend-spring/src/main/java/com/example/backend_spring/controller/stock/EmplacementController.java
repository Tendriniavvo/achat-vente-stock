package com.example.backend_spring.controller.stock;

import com.example.backend_spring.service.stock.EmplacementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emplacements")
@CrossOrigin(origins = "*")
public class EmplacementController {

    private final EmplacementService emplacementService;

    public EmplacementController(EmplacementService emplacementService) {
        this.emplacementService = emplacementService;
    }
}
