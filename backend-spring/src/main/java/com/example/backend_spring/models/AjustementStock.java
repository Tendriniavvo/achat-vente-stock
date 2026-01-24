package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "ajustements_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AjustementStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "inventaire_id")
    private Inventaire inventaire;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "quantite_ajustee", nullable = false)
    private int quantiteAjustee;

    @Column(length = 20)
    private String type = "positif"; // positif, negatif

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motif;

    @Column(length = 50)
    private String statut = "propose"; // propose, valide, rejete

    @ManyToOne
    @JoinColumn(name = "utilisateur_proposition")
    private Utilisateur utilisateurProposition;

    @ManyToOne
    @JoinColumn(name = "utilisateur_validation")
    private Utilisateur utilisateurValidation;

    @Column(name = "date_ajustement")
    private LocalDateTime dateAjustement = LocalDateTime.now();
}
