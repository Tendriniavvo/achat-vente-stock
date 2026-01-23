package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Emplacement;
import com.example.backend_spring.services.EmplacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emplacements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmplacementController {

    private final EmplacementService emplacementService;

    @GetMapping
    public List<Emplacement> getAllEmplacements() {
        return emplacementService.getAllEmplacements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emplacement> getEmplacementById(@PathVariable int id) {
        return emplacementService.getEmplacementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Emplacement saveEmplacement(@RequestBody Emplacement emplacement) {
        return emplacementService.saveEmplacement(emplacement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmplacement(@PathVariable int id) {
        emplacementService.deleteEmplacement(id);
        return ResponseEntity.noContent().build();
    }
}
