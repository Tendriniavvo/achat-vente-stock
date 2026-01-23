package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Inventaire;
import com.example.backend_spring.services.InventaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventaires")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventaireController {

    private final InventaireService inventaireService;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventaire(@PathVariable int id) {
        inventaireService.deleteInventaire(id);
        return ResponseEntity.noContent().build();
    }
}
