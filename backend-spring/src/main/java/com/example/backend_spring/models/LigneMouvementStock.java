package com.example.backend_spring.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes_mouvements_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneMouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "mouvement_id", nullable = false)
    private MouvementStock mouvement;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(nullable = false)
    private int quantite;

    @Column(name = "cout_unitaire", precision = 10, scale = 2)
    private BigDecimal coutUnitaire;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @ManyToOne
    @JoinColumn(name = "emplacement_id")
    private Emplacement emplacement;
}
