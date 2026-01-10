package com.example.backend_spring.controller.achat;

import com.example.backend_spring.service.achat.LigneBonCommandeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-bon-commande")
@CrossOrigin(origins = "*")
public class LigneBonCommandeController {

    private final LigneBonCommandeService ligneBonCommandeService;

    public LigneBonCommandeController(LigneBonCommandeService ligneBonCommandeService) {
        this.ligneBonCommandeService = ligneBonCommandeService;
    }
}
