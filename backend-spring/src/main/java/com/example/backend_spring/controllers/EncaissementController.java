package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Encaissement;
import com.example.backend_spring.services.EncaissementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encaissements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EncaissementController {

    private final EncaissementService encaissementService;

    @GetMapping
    public List<Encaissement> getAllEncaissements() {
        return encaissementService.getAllEncaissements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Encaissement> getEncaissementById(@PathVariable int id) {
        return encaissementService.getEncaissementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Encaissement saveEncaissement(@RequestBody Encaissement encaissement) {
        return encaissementService.saveEncaissement(encaissement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEncaissement(@PathVariable int id) {
        encaissementService.deleteEncaissement(id);
        return ResponseEntity.noContent().build();
    }
}
