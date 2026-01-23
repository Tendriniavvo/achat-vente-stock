package com.example.backend_spring.controllers;

import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.services.CommandeClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes-client")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommandeClientController {

    private final CommandeClientService commandeClientService;

    @GetMapping
    public List<CommandeClient> getAllCommandes() {
        return commandeClientService.getAllCommandes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeClient> getCommandeById(@PathVariable int id) {
        return commandeClientService.getCommandeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<CommandeClient> getCommandeByReference(@PathVariable String reference) {
        return commandeClientService.getCommandeByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CommandeClient saveCommande(@RequestBody CommandeClient commande) {
        return commandeClientService.saveCommande(commande);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable int id) {
        commandeClientService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }
}
