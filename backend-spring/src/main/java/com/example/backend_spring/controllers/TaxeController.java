package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Taxe;
import com.example.backend_spring.services.TaxeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaxeController {

    private final TaxeService taxeService;

    @GetMapping
    public List<Taxe> getAllTaxes() {
        return taxeService.getAllTaxes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Taxe> getTaxeById(@PathVariable int id) {
        return taxeService.getTaxeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Taxe saveTaxe(@RequestBody Taxe taxe) {
        return taxeService.saveTaxe(taxe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaxe(@PathVariable int id) {
        taxeService.deleteTaxe(id);
        return ResponseEntity.noContent().build();
    }
}
