package com.example.backend_spring.dto.article;

import java.math.BigDecimal;

public class ArticleDto {
    private Integer id;
    private String code;
    private String nom;
    private String description;
    private BigDecimal prixAchat;
    private BigDecimal prixVente;

    // Constructeurs
    public ArticleDto() {
    }

    public ArticleDto(Integer id, String code, String nom, String description, BigDecimal prixAchat,
            BigDecimal prixVente) {
        this.id = id;
        this.code = code;
        this.nom = nom;
        this.description = description;
        this.prixAchat = prixAchat;
        this.prixVente = prixVente;
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
}
