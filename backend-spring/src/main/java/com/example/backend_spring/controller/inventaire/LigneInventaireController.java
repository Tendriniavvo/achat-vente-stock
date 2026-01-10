package com.example.backend_spring.controller.inventaire;

import com.example.backend_spring.service.inventaire.LigneInventaireService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-inventaire")
@CrossOrigin(origins = "*")
public class LigneInventaireController {

    private final LigneInventaireService ligneInventaireService;

    public LigneInventaireController(LigneInventaireService ligneInventaireService) {
        this.ligneInventaireService = ligneInventaireService;
    }
}
