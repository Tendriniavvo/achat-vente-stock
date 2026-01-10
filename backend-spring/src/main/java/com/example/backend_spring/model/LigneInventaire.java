package com.example.backend_spring.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lignes_inventaires")
public class LigneInventaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    // Constructeurs
    public LigneInventaire() {
    }

    public LigneInventaire(Inventaire inventaire, Article article) {
        this.inventaire = inventaire;
        this.article = article;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Integer getQuantiteTheorique() {
        return quantiteTheorique;
    }

    public void setQuantiteTheorique(Integer quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique;
    }

    public Integer getQuantitePhysique() {
        return quantitePhysique;
    }

    public void setQuantitePhysique(Integer quantitePhysique) {
        this.quantitePhysique = quantitePhysique;
    }

    public Integer getEcart() {
        return ecart;
    }

    public void setEcart(Integer ecart) {
        this.ecart = ecart;
    }

    public String getMotifEcart() {
        return motifEcart;
    }

    public void setMotifEcart(String motifEcart) {
        this.motifEcart = motifEcart;
    }
}
