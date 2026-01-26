package com.example.backend_spring.controllers;

import com.example.backend_spring.models.MouvementStock;
import com.example.backend_spring.services.MouvementStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mouvements-stock")
@RequiredArgsConstructor
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    @GetMapping
    public List<MouvementStock> getAllMouvements() {
        return mouvementStockService.getAllMouvements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MouvementStock> getMouvementById(@PathVariable int id) {
        return mouvementStockService.getMouvementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MouvementStock saveMouvement(@RequestBody MouvementStock mouvement) {
        return mouvementStockService.saveMouvement(mouvement);
    }

    @PostMapping("/{id}/valider")
    public ResponseEntity<MouvementStock> validerMouvement(@PathVariable int id) {
        try {
            return ResponseEntity.ok(mouvementStockService.validerMouvement(id));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("introuvable")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().header("X-Error-Message", String.valueOf(e.getMessage())).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", String.valueOf(e.getMessage())).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMouvement(@PathVariable int id) {
        mouvementStockService.deleteMouvement(id);
        return ResponseEntity.noContent().build();
    }
}
