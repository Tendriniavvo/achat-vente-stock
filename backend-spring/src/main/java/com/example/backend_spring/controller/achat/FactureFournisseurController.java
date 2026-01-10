package com.example.backend_spring.controller.achat;

import com.example.backend_spring.service.achat.FactureFournisseurService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/factures-fournisseur")
@CrossOrigin(origins = "*")
public class FactureFournisseurController {

    private final FactureFournisseurService factureFournisseurService;

    public FactureFournisseurController(FactureFournisseurService factureFournisseurService) {
        this.factureFournisseurService = factureFournisseurService;
    }
}
