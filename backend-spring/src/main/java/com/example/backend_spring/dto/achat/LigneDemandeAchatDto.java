package com.example.backend_spring.dto.achat;

import java.math.BigDecimal;

public class LigneDemandeAchatDto {
    private Integer articleId;
    private Integer quantite;
    private BigDecimal prixEstime;

    // Constructeurs
    public LigneDemandeAchatDto() {
    }

    public LigneDemandeAchatDto(Integer articleId, Integer quantite, BigDecimal prixEstime) {
        this.articleId = articleId;
        this.quantite = quantite;
        this.prixEstime = prixEstime;
    }

    // Getters et Setters
    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixEstime() {
        return prixEstime;
    }

    public void setPrixEstime(BigDecimal prixEstime) {
        this.prixEstime = prixEstime;
    }
}
