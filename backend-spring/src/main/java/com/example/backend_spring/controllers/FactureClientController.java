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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable int id) {
        factureClientService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }
}
