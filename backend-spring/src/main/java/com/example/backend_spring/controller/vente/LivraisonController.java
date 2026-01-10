package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.LivraisonService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livraisons")
@CrossOrigin(origins = "*")
public class LivraisonController {

    private final LivraisonService livraisonService;

    public LivraisonController(LivraisonService livraisonService) {
        this.livraisonService = livraisonService;
    }
}
