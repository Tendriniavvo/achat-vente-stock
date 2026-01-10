package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.FactureClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/factures-client")
@CrossOrigin(origins = "*")
public class FactureClientController {

    private final FactureClientService factureClientService;

    public FactureClientController(FactureClientService factureClientService) {
        this.factureClientService = factureClientService;
    }
}
