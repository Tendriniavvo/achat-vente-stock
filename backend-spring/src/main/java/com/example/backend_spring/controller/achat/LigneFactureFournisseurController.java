package com.example.backend_spring.controller.achat;

import com.example.backend_spring.service.achat.LigneFactureFournisseurService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-facture-fournisseur")
@CrossOrigin(origins = "*")
public class LigneFactureFournisseurController {

    private final LigneFactureFournisseurService ligneFactureFournisseurService;

    public LigneFactureFournisseurController(LigneFactureFournisseurService ligneFactureFournisseurService) {
        this.ligneFactureFournisseurService = ligneFactureFournisseurService;
    }
}
