package com.example.backend_spring.controller.achat;

import com.example.backend_spring.dto.achat.BonCommandeFournisseurResponse;
import com.example.backend_spring.dto.achat.GenererBCRequest;
import com.example.backend_spring.service.achat.BonCommandeFournisseurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bons-commande-fournisseur")
@CrossOrigin(origins = "*")
public class BonCommandeFournisseurController {

    private final BonCommandeFournisseurService bonCommandeFournisseurService;

    public BonCommandeFournisseurController(BonCommandeFournisseurService bonCommandeFournisseurService) {
        this.bonCommandeFournisseurService = bonCommandeFournisseurService;
    }

    @GetMapping
    public ResponseEntity<List<BonCommandeFournisseurResponse>> getAllBonsCommande() {
        return ResponseEntity.ok(bonCommandeFournisseurService.getAllBonsCommande());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BonCommandeFournisseurResponse> getBonCommandeById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(bonCommandeFournisseurService.getBonCommandeById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/generer-depuis-da")
    public ResponseEntity<BonCommandeFournisseurResponse> genererDepuisDA(@RequestBody GenererBCRequest request) {
        try {
            BonCommandeFournisseurResponse response = bonCommandeFournisseurService.genererBonCommandeDepuisDA(
                    request.getDemandeAchatId(),
                    request.getFournisseurId(),
                    request.getUtilisateurId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
