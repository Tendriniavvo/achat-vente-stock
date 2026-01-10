package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ajustements_stock")
public class AjustementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "inventaire_id")
    private Inventaire inventaire;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "quantite_ajustee", nullable = false)
    private Integer quantiteAjustee;

    @Column(length = 20)
    private String type = "positif";

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motif;

    @Column(length = 50)
    private String statut = "propose";

    @ManyToOne
    @JoinColumn(name = "utilisateur_proposition")
    private Utilisateur utilisateurProposition;

    @ManyToOne
    @JoinColumn(name = "utilisateur_validation")
    private Utilisateur utilisateurValidation;

    @Column(name = "date_ajustement")
    private LocalDateTime dateAjustement = LocalDateTime.now();

    // Constructeurs
    public AjustementStock() {
    }

    public AjustementStock(String reference, Article article, Integer quantiteAjustee, String motif) {
        this.reference = reference;
        this.article = article;
        this.quantiteAjustee = quantiteAjustee;
        this.motif = motif;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public Integer getQuantiteAjustee() {
        return quantiteAjustee;
    }

    public void setQuantiteAjustee(Integer quantiteAjustee) {
        this.quantiteAjustee = quantiteAjustee;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Utilisateur getUtilisateurProposition() {
        return utilisateurProposition;
    }

    public void setUtilisateurProposition(Utilisateur utilisateurProposition) {
        this.utilisateurProposition = utilisateurProposition;
    }

    public Utilisateur getUtilisateurValidation() {
        return utilisateurValidation;
    }

    public void setUtilisateurValidation(Utilisateur utilisateurValidation) {
        this.utilisateurValidation = utilisateurValidation;
    }

    public LocalDateTime getDateAjustement() {
        return dateAjustement;
    }

    public void setDateAjustement(LocalDateTime dateAjustement) {
        this.dateAjustement = dateAjustement;
    }
}
