package com.example.backend_spring.controllers;

import com.example.backend_spring.models.AjustementStock;
import com.example.backend_spring.repositories.AjustementStockRepository;
import com.example.backend_spring.services.InventaireWorkflowService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ajustements-stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AjustementStockController {

    private final AjustementStockRepository ajustementStockRepository;
    private final InventaireWorkflowService inventaireWorkflowService;

    @GetMapping
    public List<AjustementStock> getAll(@RequestParam(name = "inventaireId", required = false) Integer inventaireId) {
        if (inventaireId != null) {
            return ajustementStockRepository.findByInventaireIdWithDetails(inventaireId);
        }
        return ajustementStockRepository.findAllWithDetails();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AjustementStock> getById(@PathVariable int id) {
        return ajustementStockRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/valider")
    public ResponseEntity<AjustementStock> valider(@PathVariable int id, @RequestBody ValidationRequest request) {
        try {
            return ResponseEntity.ok(inventaireWorkflowService.validerAjustement(id, request.getUtilisateurValidationId(), request.getLotId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", String.valueOf(e.getMessage())).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", String.valueOf(e.getMessage())).build();
        }
    }

    @Data
    public static class ValidationRequest {
        private Integer utilisateurValidationId;
        private Integer lotId;
    }
}
