package com.example.backend_spring.controller.vente;

import com.example.backend_spring.service.vente.CommandeClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commandes-client")
@CrossOrigin(origins = "*")
public class CommandeClientController {

    private final CommandeClientService commandeClientService;

    public CommandeClientController(CommandeClientService commandeClientService) {
        this.commandeClientService = commandeClientService;
    }
}
