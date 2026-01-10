package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.LigneCommandeClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-commande-client")
@CrossOrigin(origins = "*")
public class LigneCommandeClientController {

    private final LigneCommandeClientService ligneCommandeClientService;

    public LigneCommandeClientController(LigneCommandeClientService ligneCommandeClientService) {
        this.ligneCommandeClientService = ligneCommandeClientService;
    }
}
