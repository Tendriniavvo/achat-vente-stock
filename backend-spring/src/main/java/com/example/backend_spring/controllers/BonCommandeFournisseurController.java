package com.example.backend_spring.controllers;

import com.example.backend_spring.dto.TransformationRequest;
import com.example.backend_spring.models.BonCommandeFournisseur;
import com.example.backend_spring.services.BonCommandeFournisseurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bons-commande-fournisseur")
@RequiredArgsConstructor
@Slf4j
public class BonCommandeFournisseurController {

    private final BonCommandeFournisseurService bonCommandeFournisseurService;

    @GetMapping
    public List<BonCommandeFournisseur> getAllBonCommandes() {
        return bonCommandeFournisseurService.getAllBonCommandes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BonCommandeFournisseur> getBonCommandeById(@PathVariable int id) {
        return bonCommandeFournisseurService.getBonCommandeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<BonCommandeFournisseur> getBonCommandeByReference(@PathVariable String reference) {
        return bonCommandeFournisseurService.getBonCommandeByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public BonCommandeFournisseur saveBonCommande(@RequestBody BonCommandeFournisseur bonCommande) {
        return bonCommandeFournisseurService.saveBonCommande(bonCommande);
    }

    @PostMapping("/transformer")
    public ResponseEntity<BonCommandeFournisseur> transformer(@RequestBody TransformationRequest request) {
        log.info("Demande de transformation DA {} en BC pour acheteur {}", request.getDemandeAchatId(),
                request.getAcheteurId());
        try {
            BonCommandeFournisseur bc = bonCommandeFournisseurService.transformerEnBonCommande(
                    request.getDemandeAchatId(),
                    request.getAcheteurId(),
                    request.getFournisseurId());
            return ResponseEntity.ok(bc);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la transformation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBonCommande(@PathVariable int id) {
        bonCommandeFournisseurService.deleteBonCommande(id);
        return ResponseEntity.noContent().build();
    }
}
