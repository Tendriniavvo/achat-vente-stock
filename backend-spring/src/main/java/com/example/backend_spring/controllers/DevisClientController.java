package com.example.backend_spring.controllers;

import com.example.backend_spring.dto.DevisRequestDTO;
import com.example.backend_spring.dto.StatutUpdateRequest;
import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.models.DevisClient;
import com.example.backend_spring.models.LigneDevis;
import com.example.backend_spring.repositories.LigneDevisRepository;
import com.example.backend_spring.services.DevisClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DevisClientController {
    private final DevisClientService devisClientService;
    private final LigneDevisRepository ligneDevisRepository;

    @GetMapping
    public List<DevisClient> getAllDevis() {
        return devisClientService.getAllDevis();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DevisClient> getDevisById(@PathVariable int id) {
        return devisClientService.getDevisById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/lignes")
    public List<LigneDevis> getLignesByDevisId(@PathVariable int id) {
        return ligneDevisRepository.findByDevisId(id);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable int id, @RequestBody StatutUpdateRequest request) {
        try {
            DevisClient updated = devisClientService.updateStatut(id, request.getStatut(), request.getUtilisateurId());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/valider")
    public ResponseEntity<?> validerDevis(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        try {
            int utilisateurId = getIntFromPayload(payload, "utilisateurId");
            DevisClient validated = devisClientService.validerDevis(id, utilisateurId);
            return ResponseEntity.ok(validated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/transformer")
    public ResponseEntity<?> transformerEnCommande(@PathVariable int id,
            @RequestBody Map<String, Object> payload) {
        try {
            int utilisateurId = getIntFromPayload(payload, "utilisateurId");
            String dateLivraisonStr = (String) payload.get("dateLivraisonPrevue");

            CommandeClient commande = devisClientService.transformerEnCommande(id, utilisateurId, dateLivraisonStr);
            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private int getIntFromPayload(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Double) {
            return ((Double) value).intValue();
        } else if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        throw new IllegalArgumentException("La clé " + key + " est manquante ou invalide");
    }

    @PostMapping
    public ResponseEntity<?> createDevis(@RequestBody DevisRequestDTO dto) {
        try {
            if (dto.getClientId() == null || dto.getUtilisateurId() == null) {
                return ResponseEntity.badRequest().body("Client ID and Utilisateur ID are required");
            }

            List<LigneDevis> lignes = dto.getLignes().stream().map(l -> {
                LigneDevis ligne = new LigneDevis();
                Article article = new Article();
                article.setId(l.getArticleId());
                ligne.setArticle(article);
                ligne.setQuantite(l.getQuantite());
                ligne.setPrixUnitaire(l.getPrixUnitaire());
                ligne.setRemise(l.getRemise());
                return ligne;
            }).collect(Collectors.toList());

            DevisClient devis = devisClientService.creerDevis(
                    dto.getClientId(),
                    dto.getUtilisateurId(),
                    dto.getNotes(),
                    lignes);
            return ResponseEntity.ok(devis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevis(@PathVariable int id) {
        devisClientService.deleteDevis(id);
        return ResponseEntity.noContent().build();
    }
}
