package com.example.backend_spring.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
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
    private Boolean traceableLot = false;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column(columnDefinition = "TEXT")
    private String historique;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Constructeurs
    public Article() {
    }

    public Article(String code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategorieArticle getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieArticle categorie) {
        this.categorie = categorie;
    }

    public Unite getUnite() {
        return unite;
    }

    public void setUnite(Unite unite) {
        this.unite = unite;
    }

    public Taxe getTaxe() {
        return taxe;
    }

    public void setTaxe(Taxe taxe) {
        this.taxe = taxe;
    }

    public BigDecimal getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(BigDecimal prixAchat) {
        this.prixAchat = prixAchat;
    }

    public BigDecimal getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(BigDecimal prixVente) {
        this.prixVente = prixVente;
    }

    public String getMethodeValorisation() {
        return methodeValorisation;
    }

    public void setMethodeValorisation(String methodeValorisation) {
        this.methodeValorisation = methodeValorisation;
    }

    public Integer getStockMin() {
        return stockMin;
    }

    public void setStockMin(Integer stockMin) {
        this.stockMin = stockMin;
    }

    public Integer getStockMax() {
        return stockMax;
    }

    public void setStockMax(Integer stockMax) {
        this.stockMax = stockMax;
    }

    public Boolean getTraceableLot() {
        return traceableLot;
    }

    public void setTraceableLot(Boolean traceableLot) {
        this.traceableLot = traceableLot;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public String getHistorique() {
        return historique;
    }

    public void setHistorique(String historique) {
        this.historique = historique;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
