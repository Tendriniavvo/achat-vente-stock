package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private CategorieArticle categorie;

    @ManyToOne
    @JoinColumn(name = "unite_id")
    private Unite unite;

    @ManyToOne
    @JoinColumn(name = "taxe_id")
    private Taxe taxe;

    @Column(name = "prix_achat", precision = 10, scale = 2)
    private BigDecimal prixAchat;

    @Column(name = "prix_vente", precision = 10, scale = 2)
    private BigDecimal prixVente;

    @Column(name = "methode_valorisation", length = 10)
    private String methodeValorisation = "CUMP";

    @Column(name = "stock_min")
    private Integer stockMin;

    @Column(name = "stock_max")
    private Integer stockMax;

    @Column(name = "traceable_lot")
    private boolean traceableLot = false;

    @Column(name = "stock_strategy", length = 10)
    private String stockStrategy = "FEFO";

    private boolean actif = true;

    @Column(columnDefinition = "TEXT")
    private String historique;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();
}
