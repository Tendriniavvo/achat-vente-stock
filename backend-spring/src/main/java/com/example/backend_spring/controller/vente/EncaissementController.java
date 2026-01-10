package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.EncaissementService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/encaissements")
@CrossOrigin(origins = "*")
public class EncaissementController {

    private final EncaissementService encaissementService;

    public EncaissementController(EncaissementService encaissementService) {
        this.encaissementService = encaissementService;
    }
}
