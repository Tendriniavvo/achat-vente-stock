package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Unite;
import com.example.backend_spring.services.UniteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UniteController {

    private final UniteService uniteService;

    @GetMapping
    public List<Unite> getAllUnites() {
        return uniteService.getAllUnites();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unite> getUniteById(@PathVariable int id) {
        return uniteService.getUniteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Unite saveUnite(@RequestBody Unite unite) {
        return uniteService.saveUnite(unite);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnite(@PathVariable int id) {
        uniteService.deleteUnite(id);
        return ResponseEntity.noContent().build();
    }
}
