package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.LigneLivraisonService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-livraison")
@CrossOrigin(origins = "*")
public class LigneLivraisonController {

    private final LigneLivraisonService ligneLivraisonService;

    public LigneLivraisonController(LigneLivraisonService ligneLivraisonService) {
        this.ligneLivraisonService = ligneLivraisonService;
    }
}
