package com.example.backend_spring.controllers;

import com.example.backend_spring.models.FactureFournisseur;
import com.example.backend_spring.services.FactureFournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures-fournisseur")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable int id) {
        factureFournisseurService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }
}
