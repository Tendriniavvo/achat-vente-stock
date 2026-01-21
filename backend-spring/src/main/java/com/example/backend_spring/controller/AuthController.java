package com.example.backend_spring.controller;

import com.example.backend_spring.dto.AuthResponse;
import com.example.backend_spring.dto.LoginRequest;
import com.example.backend_spring.dto.RegisterRequest;
import com.example.backend_spring.model.Departement;
import com.example.backend_spring.model.Role;
import com.example.backend_spring.model.Utilisateur;
import com.example.backend_spring.repository.security.DepartementRepository;
import com.example.backend_spring.service.security.RoleService;
import com.example.backend_spring.service.security.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UtilisateurService utilisateurService;
    private final RoleService roleService;
    private final DepartementRepository departementRepository;

    public AuthController(UtilisateurService utilisateurService, RoleService roleService,
            DepartementRepository departementRepository) {
        this.utilisateurService = utilisateurService;
        this.roleService = roleService;
        this.departementRepository = departementRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            Utilisateur utilisateur = utilisateurService.findByEmail(request.getEmail());

            if (utilisateur == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, null, null, "Email ou mot de passe incorrect", false));
            }

            // TODO: Implémenter la vérification du mot de passe hashé avec BCrypt
            if (!utilisateur.getMotDePasse().equals(request.getMotDePasse())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, null, null, "Email ou mot de passe incorrect", false));
            }

            if (!utilisateur.getActif()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthResponse(null, null, null, null, "Compte désactivé", false));
            }

            // Mise à jour de la date de dernière connexion
            utilisateur.setDateDerniereConnexion(LocalDateTime.now());
            utilisateurService.save(utilisateur);

            return ResponseEntity.ok(new AuthResponse(
                    utilisateur.getId(),
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getEmail(),
                    "Connexion réussie",
                    true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null, null, null, "Erreur serveur: " + e.getMessage(), false));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            // Vérifier si l'email existe déjà
            if (utilisateurService.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new AuthResponse(null, null, null, null, "Cet email est déjà utilisé", false));
            }

            // Créer le nouvel utilisateur
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setNom(request.getNom());
            utilisateur.setPrenom(request.getPrenom());
            utilisateur.setEmail(request.getEmail());
            // TODO: Hasher le mot de passe avec BCrypt
            utilisateur.setMotDePasse(request.getMotDePasse());
            utilisateur.setActif(true);
            utilisateur.setDateCreation(LocalDateTime.now());

            // Ajouter le département si spécifié
            if (request.getDepartementId() != null) {
                Departement departement = departementRepository.findById(request.getDepartementId())
                        .orElseThrow(() -> new RuntimeException("Département introuvable"));
                utilisateur.setDepartement(departement);
            }

            // Ajouter les rôles sélectionnés
            if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
                Set<Role> roles = new HashSet<>();
                for (Integer roleId : request.getRoleIds()) {
                    roleService.findById(roleId).ifPresent(roles::add);
                }
                utilisateur.setRoles(roles);
            }

            Utilisateur savedUser = utilisateurService.save(utilisateur);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(
                            savedUser.getId(),
                            savedUser.getNom(),
                            savedUser.getPrenom(),
                            savedUser.getEmail(),
                            "Inscription réussie",
                            true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse(null, null, null, null, "Erreur serveur: " + e.getMessage(), false));
        }
    }
}
