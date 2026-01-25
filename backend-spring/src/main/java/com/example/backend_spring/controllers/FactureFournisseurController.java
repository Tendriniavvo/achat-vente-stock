package com.example.backend_spring.controllers;

import com.example.backend_spring.models.FactureFournisseur;
import com.example.backend_spring.models.LigneFactureFournisseur;
import com.example.backend_spring.services.FactureFournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures-fournisseur")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH })
public class FactureFournisseurController {

    private final FactureFournisseurService factureFournisseurService;

    @GetMapping
    public List<FactureFournisseur> getAllFactures() {
        return factureFournisseurService.getAllFactures();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureFournisseur> getFactureById(@PathVariable int id) {
        return factureFournisseurService.getFactureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/lignes")
    public List<LigneFactureFournisseur> getLignesByFactureId(@PathVariable int id) {
        return factureFournisseurService.getLignesByFactureId(id);
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<FactureFournisseur> getFactureByReference(@PathVariable String reference) {
        return factureFournisseurService.getFactureByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FactureFournisseur saveFacture(@RequestBody FactureFournisseur facture) {
        return factureFournisseurService.saveFacture(facture);
    }

    @PostMapping("/generer/{receptionId}")
    public ResponseEntity<FactureFournisseur> genererFacture(@PathVariable int receptionId) {
        try {
            FactureFournisseur facture = factureFournisseurService.genererFactureDepuisReception(receptionId);
            return ResponseEntity.ok(facture);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/rapprocher")
    public ResponseEntity<FactureFournisseur> rapprocherFacture(@PathVariable int id, @RequestParam int utilisateurId) {
        try {
            FactureFournisseur facture = factureFournisseurService.rapprocherFacture(id, utilisateurId);
            return ResponseEntity.ok(facture);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/payer")
    public ResponseEntity<FactureFournisseur> payerFacture(@PathVariable int id, @RequestParam int utilisateurId) {
        try {
            FactureFournisseur facture = factureFournisseurService.payerFacture(id, utilisateurId);
            return ResponseEntity.ok(facture);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable int id) {
        factureFournisseurService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }
}
