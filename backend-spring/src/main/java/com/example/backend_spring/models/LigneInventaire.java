package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lignes_inventaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneInventaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "inventaire_id")
    private Inventaire inventaire;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "quantite_theorique")
    private Integer quantiteTheorique;

    @Column(name = "quantite_physique")
    private Integer quantitePhysique;

    private Integer ecart;

    @Column(name = "motif_ecart", columnDefinition = "TEXT")
    private String motifEcart;
}
