package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_factures_clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneFactureClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "facture_id")
    private FactureClient facture;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private int quantite;

    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @ManyToOne
    @JoinColumn(name = "taxe_id")
    private Taxe taxe;
}
