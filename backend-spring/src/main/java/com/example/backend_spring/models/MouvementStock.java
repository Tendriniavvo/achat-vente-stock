package com.example.backend_spring.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mouvements_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MouvementStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, length = 50)
    private String reference;

    @Column(nullable = false, length = 50)
    private String type; // entree, sortie, transfert, ajustement

    @Column(length = 30)
    private String statut = "BROUILLON"; // BROUILLON, VALIDE, ANNULE

    @ManyToOne
    @JoinColumn(name = "depot_destination_id")
    private Depot depotDestination;

    @ManyToOne
    @JoinColumn(name = "emplacement_destination_id")
    private Emplacement emplacementDestination;

    @JsonManagedReference
    @OneToMany(mappedBy = "mouvement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneMouvementStock> lignes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column
    private Integer quantite;

    @Column(precision = 10, scale = 2)
    private BigDecimal cout;

    @Column(name = "date_mouvement")
    private LocalDateTime dateMouvement = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @ManyToOne
    @JoinColumn(name = "emplacement_id")
    private Emplacement emplacement;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Column(name = "reference_document", length = 50)
    private String referenceDocument;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(columnDefinition = "TEXT")
    private String motif;
}
