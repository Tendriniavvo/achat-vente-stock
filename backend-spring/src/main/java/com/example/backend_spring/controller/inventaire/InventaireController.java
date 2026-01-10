package com.example.backend_spring.controller.inventaire;

import com.example.backend_spring.service.inventaire.InventaireService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventaires")
@CrossOrigin(origins = "*")
public class InventaireController {

    private final InventaireService inventaireService;

    public InventaireController(InventaireService inventaireService) {
        this.inventaireService = inventaireService;
    }
}
