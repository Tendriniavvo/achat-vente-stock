package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Depot;
import com.example.backend_spring.services.DepotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depots")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DepotController {

    private final DepotService depotService;

    @GetMapping
    public List<Depot> getAllDepots() {
        return depotService.getAllDepots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Depot> getDepotById(@PathVariable int id) {
        return depotService.getDepotById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Depot saveDepot(@RequestBody Depot depot) {
        return depotService.saveDepot(depot);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepot(@PathVariable int id) {
        depotService.deleteDepot(id);
        return ResponseEntity.noContent().build();
    }
}
