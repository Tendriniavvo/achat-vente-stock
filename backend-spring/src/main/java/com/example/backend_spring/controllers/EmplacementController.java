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

    @GetMapping("/depot/{depotId}")
    public List<Emplacement> getEmplacementsByDepot(@PathVariable int depotId) {
        return emplacementService.getEmplacementsByDepot(depotId);
    }

    @GetMapping("/depot/{depotId}/root")
    public List<Emplacement> getRootEmplacementsByDepot(@PathVariable int depotId) {
        return emplacementService.getRootEmplacementsByDepot(depotId);
    }

    @GetMapping("/{id}/children")
    public List<Emplacement> getChildren(@PathVariable int id) {
        return emplacementService.getChildren(id);
    }

    @PostMapping
    public Emplacement saveEmplacement(@RequestBody Emplacement emplacement) {
        return emplacementService.saveEmplacement(emplacement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emplacement> updateEmplacement(@PathVariable int id, @RequestBody Emplacement details) {
        return ResponseEntity.ok(emplacementService.updateEmplacement(id, details));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmplacement(@PathVariable int id) {
        emplacementService.deleteEmplacement(id);
        return ResponseEntity.noContent().build();
    }
}
