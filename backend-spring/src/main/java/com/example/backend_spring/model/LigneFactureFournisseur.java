package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_factures_fournisseur")
public class LigneFactureFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "facture_id")
    private FactureFournisseur facture;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    private Integer quantite;

    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @ManyToOne
    @JoinColumn(name = "taxe_id")
    private Taxe taxe;

    // Constructeurs
    public LigneFactureFournisseur() {
    }

    public LigneFactureFournisseur(FactureFournisseur facture, Article article, Integer quantite,
            BigDecimal prixUnitaire) {
        this.facture = facture;
        this.article = article;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FactureFournisseur getFacture() {
        return facture;
    }

    public void setFacture(FactureFournisseur facture) {
        this.facture = facture;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Taxe getTaxe() {
        return taxe;
    }

    public void setTaxe(Taxe taxe) {
        this.taxe = taxe;
    }
}
