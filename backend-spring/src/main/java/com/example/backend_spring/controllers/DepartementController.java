package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Departement;
import com.example.backend_spring.services.DepartementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DepartementController {

    private final DepartementService departementService;

    @GetMapping
    public List<Departement> getAllDepartements() {
        return departementService.getAllDepartements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartementById(@PathVariable int id) {
        return departementService.getDepartementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Departement saveDepartement(@RequestBody Departement departement) {
        return departementService.saveDepartement(departement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable int id) {
        departementService.deleteDepartement(id);
        return ResponseEntity.noContent().build();
    }
}
