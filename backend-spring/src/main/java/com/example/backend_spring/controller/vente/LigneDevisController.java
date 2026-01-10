package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.LigneDevisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-devis")
@CrossOrigin(origins = "*")
public class LigneDevisController {

    private final LigneDevisService ligneDevisService;

    public LigneDevisController(LigneDevisService ligneDevisService) {
        this.ligneDevisService = ligneDevisService;
    }
}
