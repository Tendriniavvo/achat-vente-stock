package com.example.backend_spring.dto.achat;

import java.math.BigDecimal;

public class LigneBonCommandeDto {
    private Integer id;
    private Integer articleId;
    private String articleNom;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal remise;

    public LigneBonCommandeDto() {}

    public LigneBonCommandeDto(Integer id, Integer articleId, String articleNom, Integer quantite, BigDecimal prixUnitaire, BigDecimal remise) {
        this.id = id;
        this.articleId = articleId;
        this.articleNom = articleNom;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.remise = remise;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getArticleId() { return articleId; }
    public void setArticleId(Integer articleId) { this.articleId = articleId; }
    public String getArticleNom() { return articleNom; }
    public void setArticleNom(String articleNom) { this.articleNom = articleNom; }
    public Integer getQuantite() { return quantite; }
    public void setQuantite(Integer quantite) { this.quantite = quantite; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
    public BigDecimal getRemise() { return remise; }
    public void setRemise(BigDecimal remise) { this.remise = remise; }
}
