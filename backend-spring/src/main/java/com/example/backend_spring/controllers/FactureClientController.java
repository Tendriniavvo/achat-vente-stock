package com.example.backend_spring.controllers;

import com.example.backend_spring.models.FactureClient;
import com.example.backend_spring.services.FactureClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures-client")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FactureClientController {

    private final FactureClientService factureClientService;

    @GetMapping
    public List<FactureClient> getAllFactures() {
        return factureClientService.getAllFactures();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureClient> getFactureById(@PathVariable int id) {
        return factureClientService.getFactureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<FactureClient> getFactureByReference(@PathVariable String reference) {
        return factureClientService.getFactureByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FactureClient saveFacture(@RequestBody FactureClient facture) {
        return factureClientService.saveFacture(facture);
    }

    @PostMapping("/generer/{livraisonId}")
    public ResponseEntity<FactureClient> genererFacture(@PathVariable int livraisonId,
            @RequestParam int utilisateurId) {
        try {
            FactureClient facture = factureClientService.genererFactureDepuisLivraison(livraisonId, utilisateurId);
            return ResponseEntity.ok(facture);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/valider")
    public ResponseEntity<FactureClient> validerFacture(@PathVariable int id, @RequestParam int utilisateurId) {
        try {
            FactureClient facture = factureClientService.validerFacture(id, utilisateurId);
            return ResponseEntity.ok(facture);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/avoir")
    public ResponseEntity<FactureClient> creerAvoir(@PathVariable int id,
            @RequestParam String motif,
            @RequestParam int utilisateurId,
            @RequestBody(required = false) List<FactureClientService.LigneAvoirRequest> lignesAvoir) {
        try {
            FactureClient avoir = factureClientService.creerAvoir(id, motif, utilisateurId, lignesAvoir);
            return ResponseEntity.ok(avoir);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}/lignes")
    public List<com.example.backend_spring.models.LigneFactureClient> getLignes(@PathVariable int id) {
        return factureClientService.getLignesByFactureId(id);
    }

    @GetMapping("/livraison/{livraisonId}")
    public ResponseEntity<FactureClient> getFactureByLivraisonId(@PathVariable int livraisonId) {
        return factureClientService.getFactureByLivraisonId(livraisonId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable int id) {
        factureClientService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }
}
