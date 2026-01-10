package com.example.backend_spring.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lignes_receptions")
public class LigneReception {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "reception_id")
    private Reception reception;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "quantite_recue", nullable = false)
    private Integer quantiteRecue;

    @ManyToOne
    @JoinColumn(name = "lot_id")
    private Lot lot;

    @Column(columnDefinition = "TEXT")
    private String ecart;

    // Constructeurs
    public LigneReception() {
    }

    public LigneReception(Reception reception, Article article, Integer quantiteRecue) {
        this.reception = reception;
        this.article = article;
        this.quantiteRecue = quantiteRecue;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Reception getReception() {
        return reception;
    }

    public void setReception(Reception reception) {
        this.reception = reception;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Integer getQuantiteRecue() {
        return quantiteRecue;
    }

    public void setQuantiteRecue(Integer quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
    }

    public Lot getLot() {
        return lot;
    }

    public void setLot(Lot lot) {
        this.lot = lot;
    }

    public String getEcart() {
        return ecart;
    }

    public void setEcart(String ecart) {
        this.ecart = ecart;
    }
}
