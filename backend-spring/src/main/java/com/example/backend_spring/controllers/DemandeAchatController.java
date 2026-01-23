package com.example.backend_spring.controllers;

import com.example.backend_spring.dto.DemandeAchatDTO;
import com.example.backend_spring.models.DemandeAchat;
import com.example.backend_spring.models.LigneDemandeAchat;
import com.example.backend_spring.services.DemandeAchatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demandes-achat")
@RequiredArgsConstructor
public class DemandeAchatController {

    private final DemandeAchatService demandeAchatService;

    @GetMapping
    public List<DemandeAchat> getAllDemandes() {
        return demandeAchatService.getAllDemandes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDemandeById(@PathVariable int id) {
        return demandeAchatService.getDemandeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/lignes")
    public List<LigneDemandeAchat> getLignesByDemandeId(@PathVariable int id) {
        return demandeAchatService.getLignesByDemandeId(id);
    }

    @PostMapping
    public ResponseEntity<?> createDemande(@RequestBody DemandeAchatDTO dto) {
        try {
            DemandeAchat demande = demandeAchatService.createDemande(dto);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDemande(@PathVariable int id, @RequestBody DemandeAchatDTO dto) {
        try {
            DemandeAchat demande = demandeAchatService.updateDemande(id, dto);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/soumettre")
    public ResponseEntity<?> soumettre(@PathVariable int id) {
        try {
            DemandeAchat demande = demandeAchatService.updateStatus(id, "en attente", null);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/verifier-fonds")
    public ResponseEntity<?> verifierFonds(@PathVariable int id) {
        try {
            DemandeAchat demande = demandeAchatService.verifierFonds(id);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/approuver")
    public ResponseEntity<?> approuver(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        try {
            Integer validateurId = body.get("validateurId");
            if (validateurId == null) {
                return ResponseEntity.badRequest().body("ID du validateur manquant");
            }
            DemandeAchat demande = demandeAchatService.approuver(id, validateurId);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/rejeter")
    public ResponseEntity<?> rejeter(@PathVariable int id, @RequestBody Map<String, String> body) {
        try {
            String motif = body.get("motif");
            DemandeAchat demande = demandeAchatService.updateStatus(id, "rejeté", motif);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<?> annuler(@PathVariable int id) {
        try {
            DemandeAchat demande = demandeAchatService.updateStatus(id, "annulé", null);
            return ResponseEntity.ok(demande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDemande(@PathVariable int id) {
        try {
            demandeAchatService.deleteDemande(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
