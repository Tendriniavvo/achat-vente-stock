package com.example.backend_spring.controller.stock;

import com.example.backend_spring.dto.stock.DepotRequest;
import com.example.backend_spring.dto.stock.DepotResponse;
import com.example.backend_spring.service.stock.DepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depots")
@CrossOrigin(origins = "http://localhost:5173")
public class DepotController {

    @Autowired
    private DepotService depotService;

    // Créer un nouveau dépôt
    @PostMapping
    public ResponseEntity<DepotResponse> createDepot(@RequestBody DepotRequest request) {
        try {
            DepotResponse response = depotService.createDepot(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer tous les dépôts
    @GetMapping
    public ResponseEntity<List<DepotResponse>> getAllDepots(
            @RequestParam(required = false) Boolean actif) {
        try {
            List<DepotResponse> depots;
            if (actif != null && actif) {
                depots = depotService.getDepotsActifs();
            } else {
                depots = depotService.getAllDepots();
            }
            return new ResponseEntity<>(depots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un dépôt par ID
    @GetMapping("/{id}")
    public ResponseEntity<DepotResponse> getDepotById(@PathVariable Integer id) {
        try {
            DepotResponse depot = depotService.getDepotById(id);
            return new ResponseEntity<>(depot, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour un dépôt
    @PutMapping("/{id}")
    public ResponseEntity<DepotResponse> updateDepot(
            @PathVariable Integer id,
            @RequestBody DepotRequest request) {
        try {
            DepotResponse response = depotService.updateDepot(id, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprimer un dépôt
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDepot(@PathVariable Integer id) {
        try {
            depotService.deleteDepot(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Activer/Désactiver un dépôt
    @PostMapping("/{id}/toggle-actif")
    public ResponseEntity<DepotResponse> toggleActif(@PathVariable Integer id) {
        try {
            DepotResponse response = depotService.toggleActif(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Rechercher des dépôts par nom
    @GetMapping("/search")
    public ResponseEntity<List<DepotResponse>> searchDepots(
            @RequestParam String nom) {
        try {
            List<DepotResponse> depots = depotService.searchDepotsByNom(nom);
            return new ResponseEntity<>(depots, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
