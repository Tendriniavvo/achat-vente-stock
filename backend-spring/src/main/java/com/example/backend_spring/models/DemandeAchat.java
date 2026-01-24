package com.example.backend_spring.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "demandes_achat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandeAchat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private Utilisateur demandeur;

    @OneToMany(mappedBy = "demandeAchat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<LigneDemandeAchat> lignes;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(length = 50)
    private String statut = "brouillon"; // brouillon, soumise, validee, rejetee

    @Column(name = "motif_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    @Column(name = "historique_validations", columnDefinition = "TEXT")
    private String historiqueValidations;
}
