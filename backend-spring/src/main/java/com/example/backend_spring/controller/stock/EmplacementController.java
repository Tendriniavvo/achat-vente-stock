package com.example.backend_spring.controller.stock;

import com.example.backend_spring.dto.stock.EmplacementRequest;
import com.example.backend_spring.dto.stock.EmplacementResponse;
import com.example.backend_spring.service.stock.EmplacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emplacements")
@CrossOrigin(origins = "http://localhost:5173")
public class EmplacementController {

    @Autowired
    private EmplacementService emplacementService;

    // Créer un nouvel emplacement
    @PostMapping
    public ResponseEntity<EmplacementResponse> createEmplacement(@RequestBody EmplacementRequest request) {
        try {
            EmplacementResponse response = emplacementService.createEmplacement(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer tous les emplacements ou filtrer par dépôt
    @GetMapping
    public ResponseEntity<List<EmplacementResponse>> getAllEmplacements(
            @RequestParam(required = false) Integer depotId) {
        try {
            List<EmplacementResponse> emplacements;
            if (depotId != null) {
                emplacements = emplacementService.getEmplacementsByDepot(depotId);
            } else {
                emplacements = emplacementService.getAllEmplacements();
            }
            return new ResponseEntity<>(emplacements, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Récupérer un emplacement par ID
    @GetMapping("/{id}")
    public ResponseEntity<EmplacementResponse> getEmplacementById(@PathVariable Integer id) {
        try {
            EmplacementResponse emplacement = emplacementService.getEmplacementById(id);
            return new ResponseEntity<>(emplacement, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mettre à jour un emplacement
    @PutMapping("/{id}")
    public ResponseEntity<EmplacementResponse> updateEmplacement(
            @PathVariable Integer id,
            @RequestBody EmplacementRequest request) {
        try {
            EmplacementResponse response = emplacementService.updateEmplacement(id, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Supprimer un emplacement
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteEmplacement(@PathVariable Integer id) {
        try {
            emplacementService.deleteEmplacement(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Rechercher des emplacements par code
    @GetMapping("/search")
    public ResponseEntity<List<EmplacementResponse>> searchEmplacements(
            @RequestParam String code,
            @RequestParam(required = false) Integer depotId) {
        try {
            List<EmplacementResponse> emplacements;
            if (depotId != null) {
                emplacements = emplacementService.searchEmplacementsByCodeAndDepot(depotId, code);
            } else {
                emplacements = emplacementService.searchEmplacementsByCode(code);
            }
            return new ResponseEntity<>(emplacements, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
