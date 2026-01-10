package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.LigneFactureClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-facture-client")
@CrossOrigin(origins = "*")
public class LigneFactureClientController {

    private final LigneFactureClientService ligneFactureClientService;

    public LigneFactureClientController(LigneFactureClientService ligneFactureClientService) {
        this.ligneFactureClientService = ligneFactureClientService;
    }
}
