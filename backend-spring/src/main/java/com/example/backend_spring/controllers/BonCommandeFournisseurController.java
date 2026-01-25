package com.example.backend_spring.controllers;

import com.example.backend_spring.dto.TransformationRequest;
import com.example.backend_spring.models.BonCommandeFournisseur;
import com.example.backend_spring.services.BonCommandeFournisseurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
                    request.getFournisseurId(),
                    request.getDateLivraisonPrevue());
            return ResponseEntity.ok(bc);
        } catch (RuntimeException e) {
            log.error("Erreur lors de la transformation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}/lignes")
    public List<com.example.backend_spring.models.LigneBonCommande> getLignes(@PathVariable int id) {
        return bonCommandeFournisseurService.getLignesByBcId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BonCommandeFournisseur> updateBC(@PathVariable int id,
            @RequestBody BonCommandeFournisseur bc) {
        try {
            return ResponseEntity.ok(bonCommandeFournisseurService.updateBonCommande(id, bc));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/lignes")
    public ResponseEntity<Void> updateLignes(@PathVariable int id,
            @RequestBody List<com.example.backend_spring.models.LigneBonCommande> lignes) {
        try {
            bonCommandeFournisseurService.updateLignes(id, lignes);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/valider")
    public ResponseEntity<BonCommandeFournisseur> valider(@PathVariable int id,
            @RequestBody Map<String, Integer> payload) {
        try {
            Integer utilisateurId = payload.get("validateurId");
            if (utilisateurId == null) {
                return ResponseEntity.badRequest().body(null);
            }
            return ResponseEntity.ok(bonCommandeFournisseurService.validerBC(id, utilisateurId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/litige")
    public ResponseEntity<BonCommandeFournisseur> signalerLitige(@PathVariable int id,
            @RequestBody Map<String, Integer> payload) {
        try {
            Integer utilisateurId = payload.get("utilisateurId");
            return ResponseEntity.ok(bonCommandeFournisseurService.signalerLitige(id, utilisateurId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/lever-litige")
    public ResponseEntity<BonCommandeFournisseur> leverLitige(@PathVariable int id,
            @RequestBody Map<String, Integer> payload) {
        try {
            Integer utilisateurId = payload.get("utilisateurId");
            return ResponseEntity.ok(bonCommandeFournisseurService.leverLitige(id, utilisateurId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/envoyer")
    public ResponseEntity<BonCommandeFournisseur> envoyer(@PathVariable int id,
            @RequestBody Map<String, Integer> payload) {
        try {
            Integer utilisateurId = payload.get("utilisateurId");
            return ResponseEntity.ok(bonCommandeFournisseurService.envoyerAuFournisseur(id, utilisateurId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBonCommande(@PathVariable int id) {
        bonCommandeFournisseurService.deleteBonCommande(id);
        return ResponseEntity.noContent().build();
    }
}
