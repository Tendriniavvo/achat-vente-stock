package com.example.backend_spring.controller.achat;

import com.example.backend_spring.service.achat.BonCommandeFournisseurService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bons-commande-fournisseur")
@CrossOrigin(origins = "*")
public class BonCommandeFournisseurController {

    private final BonCommandeFournisseurService bonCommandeFournisseurService;

    public BonCommandeFournisseurController(BonCommandeFournisseurService bonCommandeFournisseurService) {
        this.bonCommandeFournisseurService = bonCommandeFournisseurService;
    }
}
