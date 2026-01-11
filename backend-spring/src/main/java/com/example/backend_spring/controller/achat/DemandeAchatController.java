package com.example.backend_spring.controller.achat;

import com.example.backend_spring.dto.achat.DemandeAchatRequest;
import com.example.backend_spring.dto.achat.DemandeAchatResponse;
import com.example.backend_spring.service.achat.DemandeAchatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-achat")
@CrossOrigin(origins = "*")
public class DemandeAchatController {

    private final DemandeAchatService demandeAchatService;

    public DemandeAchatController(DemandeAchatService demandeAchatService) {
        this.demandeAchatService = demandeAchatService;
    }

    @PostMapping
    public ResponseEntity<DemandeAchatResponse> createDemandeAchat(@RequestBody DemandeAchatRequest request) {
        try {
            DemandeAchatResponse response = demandeAchatService.createDemandeAchat(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<DemandeAchatResponse>> getAllDemandesAchat() {
        List<DemandeAchatResponse> demandes = demandeAchatService.getAllDemandesAchat();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandeAchatResponse> getDemandeAchatById(@PathVariable Integer id) {
        try {
            DemandeAchatResponse response = demandeAchatService.getDemandeAchatById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la récupération de la demande " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DemandeAchatResponse> updateDemandeAchat(
            @PathVariable Integer id,
            @RequestBody DemandeAchatRequest request) {
        try {
            DemandeAchatResponse response = demandeAchatService.updateDemandeAchat(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de la mise à jour de la demande " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{id}/soumettre")
    public ResponseEntity<DemandeAchatResponse> soumettreValidation(@PathVariable Integer id) {
        try {
            DemandeAchatResponse response = demandeAchatService.soumettreValidation(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{id}/approuver")
    public ResponseEntity<DemandeAchatResponse> approuverDemandeAchat(
            @PathVariable Integer id,
            @RequestBody(required = false) java.util.Map<String, Integer> body) {
        try {
            Integer validateurId = body != null ? body.get("validateurId") : null;
            if (validateurId == null) {
                return ResponseEntity.badRequest().build();
            }
            DemandeAchatResponse response = demandeAchatService.approuverDemandeAchat(id, validateurId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'approbation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{id}/rejeter")
    public ResponseEntity<DemandeAchatResponse> rejeterDemandeAchat(
            @PathVariable Integer id,
            @RequestBody java.util.Map<String, Object> body) {
        try {
            Integer validateurId = body.get("validateurId") != null
                    ? Integer.parseInt(body.get("validateurId").toString())
                    : null;
            String motif = body.get("motif") != null ? body.get("motif").toString() : null;

            if (validateurId == null) {
                return ResponseEntity.badRequest().build();
            }

            DemandeAchatResponse response = demandeAchatService.rejeterDemandeAchat(id, validateurId, motif);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur lors du rejet: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerDemandeAchat(@PathVariable Integer id) {
        try {
            demandeAchatService.annulerDemandeAchat(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemandeAchat(@PathVariable Integer id) {
        try {
            demandeAchatService.deleteDemandeAchat(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
