package com.example.backend_spring.controllers;

import com.example.backend_spring.models.LigneReception;
import com.example.backend_spring.models.Reception;
import com.example.backend_spring.services.ReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReceptionController {
    private final ReceptionService receptionService;

    @GetMapping
    public List<Reception> getAllReceptions() {
        return receptionService.getAllReceptions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reception> getReceptionById(@PathVariable int id) {
        return receptionService.getReceptionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/lignes")
    public List<LigneReception> getLignesByReceptionId(@PathVariable int id) {
        return receptionService.getLignesByReceptionId(id);
    }

    @PostMapping("/enregistrer")
    public ResponseEntity<?> enregistrerReception(@RequestBody Map<String, Object> payload) {
        try {
            int bcId = getIntFromPayload(payload, "bcId");
            int utilisateurId = getIntFromPayload(payload, "utilisateurId");
            int depotId = getIntFromPayload(payload, "depotId");
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");

            Reception reception = receptionService.enregistrerReception(bcId, utilisateurId, depotId, items);
            return ResponseEntity.ok(reception);
        } catch (Exception e) {
            e.printStackTrace(); // Pour le debug
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Erreur interne du serveur"));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReception(@PathVariable int id) {
        receptionService.deleteReception(id);
        return ResponseEntity.ok().build();
    }
}