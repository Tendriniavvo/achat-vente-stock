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
@CrossOrigin(origins = "*")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMouvement(@PathVariable int id) {
        mouvementStockService.deleteMouvement(id);
        return ResponseEntity.noContent().build();
    }
}
