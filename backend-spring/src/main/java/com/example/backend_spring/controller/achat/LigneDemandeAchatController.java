package com.example.backend_spring.controller.achat;

import com.example.backend_spring.service.achat.LigneDemandeAchatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes-demande-achat")
@CrossOrigin(origins = "*")
public class LigneDemandeAchatController {

    private final LigneDemandeAchatService ligneDemandeAchatService;

    public LigneDemandeAchatController(LigneDemandeAchatService ligneDemandeAchatService) {
        this.ligneDemandeAchatService = ligneDemandeAchatService;
    }
}
