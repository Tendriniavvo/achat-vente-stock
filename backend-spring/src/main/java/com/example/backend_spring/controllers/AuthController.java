package com.example.backend_spring.controllers;

import com.example.backend_spring.dto.AuthResponse;
import com.example.backend_spring.dto.LoginRequest;
import com.example.backend_spring.dto.RegisterRequest;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UtilisateurService utilisateurService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Utilisateur> utilisateur = utilisateurService.authenticate(request.getEmail(),
                request.getMotDePasse());
        if (utilisateur.isPresent()) {
            // In a real app, you'd generate a JWT here. For now, we'll send a UUID.
            String token = UUID.randomUUID().toString();
            return ResponseEntity.ok(new AuthResponse(token, utilisateur.get()));
        } else {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        System.out.println("Requête d'inscription reçue pour: " + request.getEmail());
        System.out.println("ID Département reçu: " + request.getDepartementId());
        try {
            Utilisateur utilisateur = utilisateurService.register(request);
            System.out.println("Utilisateur enregistré avec ID: " + utilisateur.getId());

            // Recharger l'utilisateur pour être sûr d'avoir les données persistées
            utilisateur = utilisateurService.getUtilisateurById(utilisateur.getId()).orElse(utilisateur);

            if (utilisateur.getDepartement() != null) {
                System.out.println("Département final dans l'objet: " + utilisateur.getDepartement().getNom());
            } else {
                System.out.println("ATTENTION: Département final est NULL dans l'objet!");
            }
            String token = UUID.randomUUID().toString();
            return ResponseEntity.ok(new AuthResponse(token, utilisateur));
        } catch (RuntimeException e) {
            System.err.println("Erreur lors de l'inscription: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
