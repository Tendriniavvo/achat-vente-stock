package com.example.backend_spring.controllers;

import com.example.backend_spring.models.BonCommandeFournisseur;
import com.example.backend_spring.models.Fournisseur;
import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.services.FournisseurService;
import com.example.backend_spring.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FournisseurController {

    private final FournisseurService fournisseurService;
    private final UtilisateurService utilisateurService;

    @GetMapping
    public List<Fournisseur> getAllFournisseurs() {
        return fournisseurService.getAllFournisseurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fournisseur> getFournisseurById(@PathVariable int id) {
        return fournisseurService.getFournisseurById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/orders")
    public List<BonCommandeFournisseur> getFournisseurOrders(@PathVariable int id) {
        return fournisseurService.getFournisseurOrders(id);
    }

    @GetMapping("/{id}/history")
    public List<JournalAudit> getFournisseurHistory(@PathVariable int id) {
        return fournisseurService.getFournisseurHistory(id);
    }

    @GetMapping("/{id}/stats")
    public Map<String, Object> getFournisseurStats(@PathVariable int id) {
        return fournisseurService.getFournisseurStats(id);
    }

    @PostMapping
    public ResponseEntity<?> saveFournisseur(@RequestBody Fournisseur fournisseur, @RequestParam int utilisateurId) {
        return utilisateurService.getUtilisateurById(utilisateurId)
                .map(u -> ResponseEntity.ok(fournisseurService.saveFournisseur(fournisseur, u)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable int id, @RequestParam int utilisateurId) {
        System.out.println("DEBUG: Tentative de changement de statut pour le fournisseur ID: " + id + " par l'utilisateur ID: " + utilisateurId);
        return utilisateurService.getUtilisateurById(utilisateurId)
                .map(u -> {
                    System.out.println("DEBUG: Utilisateur trouvé: " + u.getNom());
                    return ResponseEntity.ok(fournisseurService.toggleStatus(id, u));
                })
                .orElseGet(() -> {
                    System.out.println("DEBUG: Utilisateur non trouvé !");
                    return ResponseEntity.badRequest().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable int id) {
        fournisseurService.deleteFournisseur(id);
        return ResponseEntity.noContent().build();
    }
}
