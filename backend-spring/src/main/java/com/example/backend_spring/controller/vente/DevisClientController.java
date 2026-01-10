package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.DevisClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devis-client")
@CrossOrigin(origins = "*")
public class DevisClientController {

    private final DevisClientService devisClientService;

    public DevisClientController(DevisClientService devisClientService) {
        this.devisClientService = devisClientService;
    }
}
