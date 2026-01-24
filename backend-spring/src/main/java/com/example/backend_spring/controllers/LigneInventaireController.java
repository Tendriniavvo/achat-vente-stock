package com.example.backend_spring.controllers;

import com.example.backend_spring.models.LigneInventaire;
import com.example.backend_spring.repositories.LigneInventaireRepository;
import com.example.backend_spring.services.InventaireWorkflowService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lignes-inventaires")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LigneInventaireController {

    private final LigneInventaireRepository ligneInventaireRepository;
    private final InventaireWorkflowService inventaireWorkflowService;

    @GetMapping
    public List<LigneInventaire> getAll(@RequestParam(name = "inventaireId", required = false) Integer inventaireId) {
        if (inventaireId != null) {
            return ligneInventaireRepository.findByInventaireId(inventaireId);
        }
        return ligneInventaireRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LigneInventaire> getById(@PathVariable int id) {
        return ligneInventaireRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/physique")
    public ResponseEntity<LigneInventaire> saisirPhysique(@PathVariable int id, @RequestBody SaisiePhysiqueRequest request) {
        try {
            return ResponseEntity.ok(inventaireWorkflowService.saisirPhysique(id, request.getQuantitePhysique(), request.getMotifEcart()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", String.valueOf(e.getMessage())).build();
        }
    }

    @Data
    public static class SaisiePhysiqueRequest {
        private Integer quantitePhysique;
        private String motifEcart;
    }
}
