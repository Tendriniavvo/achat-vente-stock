package com.example.backend_spring.controllers;

import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.models.LigneCommandeClient;
import com.example.backend_spring.repositories.LigneCommandeClientRepository;
import com.example.backend_spring.services.CommandeClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commandes-client")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommandeClientController {

    private final CommandeClientService commandeClientService;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;

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

    @GetMapping("/{id}/lignes")
    public List<LigneCommandeClient> getLignesByCommandeId(@PathVariable int id) {
        return ligneCommandeClientRepository.findByCommandeId(id);
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

    @PostMapping("/{id}/preparer-livraison")
    public ResponseEntity<?> preparerLivraison(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        try {
            Object userIdObj = payload.get("utilisateurId");
            if (userIdObj == null) {
                throw new RuntimeException("L'ID de l'utilisateur est requis.");
            }
            int utilisateurId = Integer.parseInt(userIdObj.toString());
            CommandeClient updated = commandeClientService.preparerLivraison(id, utilisateurId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable int id, @RequestBody Map<String, String> payload) {
        try {
            String statut = payload.get("statut");
            CommandeClient updated = commandeClientService.updateStatut(id, statut);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable int id) {
        commandeClientService.deleteCommande(id);
        return ResponseEntity.noContent().build();
    }
}
