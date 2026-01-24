package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commandes_clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneCommandeClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private CommandeClient commande;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(nullable = false)
    private int quantite;

    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(precision = 5, scale = 2)
    private BigDecimal remise = BigDecimal.ZERO;
}
