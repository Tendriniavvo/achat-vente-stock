package com.example.backend_spring.controller.achat;

import com.example.backend_spring.service.achat.DemandeAchatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demandes-achat")
@CrossOrigin(origins = "*")
public class DemandeAchatController {

    private final DemandeAchatService demandeAchatService;

    public DemandeAchatController(DemandeAchatService demandeAchatService) {
        this.demandeAchatService = demandeAchatService;
    }
}
