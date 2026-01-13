package com.example.backend_spring.controller;

import com.example.backend_spring.dto.DepartementDto;
import com.example.backend_spring.service.DepartementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
@CrossOrigin(origins = "http://localhost:5173")
public class DepartementController {

    private final DepartementService departementService;

    public DepartementController(DepartementService departementService) {
        this.departementService = departementService;
    }

    @GetMapping
    public ResponseEntity<List<DepartementDto>> getAllDepartements(
            @RequestParam(required = false) Boolean actif) {
        try {
            List<DepartementDto> departements;
            if (actif != null && actif) {
                departements = departementService.getDepartementsActifs();
            } else {
                departements = departementService.getAllDepartements();
            }
            return ResponseEntity.ok(departements);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartementDto> getDepartementById(@PathVariable Integer id) {
        try {
            DepartementDto departement = departementService.getDepartementById(id);
            return ResponseEntity.ok(departement);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<DepartementDto> createDepartement(@RequestBody DepartementDto dto) {
        try {
            DepartementDto created = departementService.createDepartement(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartementDto> updateDepartement(
            @PathVariable Integer id,
            @RequestBody DepartementDto dto) {
        try {
            DepartementDto updated = departementService.updateDepartement(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Integer id) {
        try {
            departementService.deleteDepartement(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
