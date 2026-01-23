package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Lot;
import com.example.backend_spring.services.LotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LotController {

    private final LotService lotService;

    @GetMapping
    public List<Lot> getAllLots() {
        return lotService.getAllLots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lot> getLotById(@PathVariable int id) {
        return lotService.getLotById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numeroLot}")
    public ResponseEntity<Lot> getLotByNumero(@PathVariable String numeroLot) {
        return lotService.getLotByNumero(numeroLot)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Lot saveLot(@RequestBody Lot lot) {
        return lotService.saveLot(lot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLot(@PathVariable int id) {
        lotService.deleteLot(id);
        return ResponseEntity.noContent().build();
    }
}
