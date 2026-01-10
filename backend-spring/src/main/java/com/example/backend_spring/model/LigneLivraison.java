package com.example.backend_spring.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lignes_livraisons")
public class LigneLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "livraison_id")
    private Livraison livraison;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "quantite_livree", nullable = false)
    private Integer quantiteLivree;

    @ManyToOne
    @JoinColumn(name = "emplacement_id")
    private Emplacement emplacement;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    // Constructeurs
    public LigneLivraison() {
    }

    public LigneLivraison(Livraison livraison, Article article, Integer quantiteLivree) {
        this.livraison = livraison;
        this.article = article;
        this.quantiteLivree = quantiteLivree;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Livraison getLivraison() {
        return livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Integer getQuantiteLivree() {
        return quantiteLivree;
    }

    public void setQuantiteLivree(Integer quantiteLivree) {
        this.quantiteLivree = quantiteLivree;
    }

    public Emplacement getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(Emplacement emplacement) {
        this.emplacement = emplacement;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }
}
