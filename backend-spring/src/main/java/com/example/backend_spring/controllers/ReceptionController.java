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
            int bcId = (int) payload.get("bcId");
            int utilisateurId = (int) payload.get("utilisateurId");
            int depotId = (int) payload.get("depotId");
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");

            Reception reception = receptionService.enregistrerReception(bcId, utilisateurId, depotId, items);
            return ResponseEntity.ok(reception);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReception(@PathVariable int id) {
        receptionService.deleteReception(id);
        return ResponseEntity.ok().build();
    }
}