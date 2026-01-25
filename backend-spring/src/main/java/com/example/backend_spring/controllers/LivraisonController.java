package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Livraison;
import com.example.backend_spring.models.LigneLivraison;
import com.example.backend_spring.services.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;

    @GetMapping
    public List<Livraison> getAllLivraisons() {
        return livraisonService.getAllLivraisons();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livraison> getLivraisonById(@PathVariable int id) {
        return livraisonService.getLivraisonById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/lignes")
    public List<LigneLivraison> getLignesByLivraisonId(@PathVariable int id) {
        return livraisonService.getLignesByLivraisonId(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLivraison(@PathVariable int id) {
        livraisonService.deleteLivraison(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/expedier")
    public ResponseEntity<?> expedierLivraison(@PathVariable int id,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            Object userIdObj = request.get("utilisateurId");
            if (userIdObj == null) {
                throw new RuntimeException("L'ID de l'utilisateur est requis.");
            }
            int utilisateurId = Integer.parseInt(userIdObj.toString());
            return ResponseEntity.ok(livraisonService.expedierLivraison(id, utilisateurId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
