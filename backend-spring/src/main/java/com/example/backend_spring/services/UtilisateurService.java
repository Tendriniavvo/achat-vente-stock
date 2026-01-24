package com.example.backend_spring.services;

import com.example.backend_spring.dto.LoginRequest;
import com.example.backend_spring.dto.RegisterRequest;
import com.example.backend_spring.models.Departement;
import com.example.backend_spring.models.Role;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.DepartementRepository;
import com.example.backend_spring.repositories.RoleRepository;
import com.example.backend_spring.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UtilisateurService {
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final DepartementRepository departementRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> getUtilisateurById(int id) {
        return utilisateurRepository.findById(id);
    }

    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    @Transactional
    public Utilisateur register(RegisterRequest request) {
        System.out.println("DEBUG: Début de l'inscription pour " + request.getEmail());
        System.out.println("DEBUG: ID Département reçu du frontend: " + request.getDepartementId());
        System.out.println("DEBUG: IDs Rôles reçus du frontend: " + request.getRoleIds());

        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            utilisateur.setRoles(roles);
            System.out.println("DEBUG: " + roles.size() + " rôles assignés.");
        }

        if (request.getDepartementId() != null) {
            Departement dept = departementRepository.findById(request.getDepartementId())
                    .orElseThrow(() -> new RuntimeException(
                            "Département non trouvé avec l'ID: " + request.getDepartementId()));
            utilisateur.setDepartement(dept);
            System.out.println("DEBUG: Département '" + dept.getNom() + "' (ID: " + dept.getId()
                    + ") assigné à l'objet Utilisateur.");
        } else {
            System.out.println("DEBUG: Aucun ID de département reçu.");
        }

        System.out.println("DEBUG: Sauvegarde de l'utilisateur en base...");
        Utilisateur savedUser = utilisateurRepository.saveAndFlush(utilisateur);

        System.out.println("DEBUG: Utilisateur sauvegardé avec succès.");
        System.out.println("DEBUG: ID final: " + savedUser.getId());
        if (savedUser.getDepartement() != null) {
            System.out.println("DEBUG: Département final en base: " + savedUser.getDepartement().getNom() + " (ID: "
                    + savedUser.getDepartement().getId() + ")");
        } else {
            System.out.println("DEBUG: ATTENTION - Le département est NULL après sauvegarde !");
        }

        return savedUser;
    }

    public Optional<Utilisateur> authenticate(String email, String password) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            if (passwordEncoder.matches(password, utilisateur.getMotDePasse())) {
                return Optional.of(utilisateur);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().startsWith("$2a$")) {
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        }
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public Utilisateur updateUtilisateur(int id, String nom, String prenom, String email, Integer departementId) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + id));

        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setEmail(email);

        if (departementId != null) {
            Departement dept = departementRepository.findById(departementId)
                    .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + departementId));
            utilisateur.setDepartement(dept);
        } else {
            utilisateur.setDepartement(null);
        }

        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void deleteUtilisateur(int id) {
        utilisateurRepository.deleteById(id);
    }
}
