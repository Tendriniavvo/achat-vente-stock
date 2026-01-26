package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Inventaire;
import com.example.backend_spring.models.AjustementStock;
import com.example.backend_spring.models.LigneInventaire;
import com.example.backend_spring.services.InventaireService;
import com.example.backend_spring.services.InventaireWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventaires")
@RequiredArgsConstructor
public class InventaireController {

    private final InventaireService inventaireService;
    private final InventaireWorkflowService inventaireWorkflowService;

    @GetMapping
    public List<Inventaire> getAllInventaires() {
        return inventaireService.getAllInventaires();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventaire> getInventaireById(@PathVariable int id) {
        return inventaireService.getInventaireById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<Inventaire> getInventaireByReference(@PathVariable String reference) {
        return inventaireService.getInventaireByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Inventaire saveInventaire(@RequestBody Inventaire inventaire) {
        return inventaireService.saveInventaire(inventaire);
    }

    @PostMapping("/{id}/generer-lignes")
    public ResponseEntity<List<LigneInventaire>> genererLignes(@PathVariable int id) {
        try {
            return ResponseEntity.ok(inventaireWorkflowService.genererLignes(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", String.valueOf(e.getMessage())).build();
        }
    }

    @GetMapping("/{id}/lignes")
    public ResponseEntity<List<LigneInventaire>> getLignes(@PathVariable int id) {
        return ResponseEntity.ok(inventaireWorkflowService.genererLignes(id));
    }

    @PostMapping("/{id}/terminer")
    public ResponseEntity<Inventaire> terminer(@PathVariable int id) {
        try {
            return ResponseEntity.ok(inventaireWorkflowService.terminerInventaire(id));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", String.valueOf(e.getMessage())).build();
        }
    }

    @PostMapping("/{id}/generer-ajustements")
    public ResponseEntity<List<AjustementStock>> genererAjustements(@PathVariable int id, @RequestBody GenererAjustementsRequest request) {
        try {
            return ResponseEntity.ok(inventaireWorkflowService.genererAjustements(id, request.getUtilisateurPropositionId(), request.getMotifParDefaut()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", String.valueOf(e.getMessage())).build();
        }
    }

    public static class GenererAjustementsRequest {
        private Integer utilisateurPropositionId;
        private String motifParDefaut;

        public Integer getUtilisateurPropositionId() {
            return utilisateurPropositionId;
        }

        public void setUtilisateurPropositionId(Integer utilisateurPropositionId) {
            this.utilisateurPropositionId = utilisateurPropositionId;
        }

        public String getMotifParDefaut() {
            return motifParDefaut;
        }

        public void setMotifParDefaut(String motifParDefaut) {
            this.motifParDefaut = motifParDefaut;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventaire(@PathVariable int id) {
        inventaireService.deleteInventaire(id);
        return ResponseEntity.noContent().build();
    }
}
