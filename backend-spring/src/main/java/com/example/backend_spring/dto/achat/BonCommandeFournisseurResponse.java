package com.example.backend_spring.dto.achat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BonCommandeFournisseurResponse {
    private Integer id;
    private String reference;
    private Integer demandeAchatId;
    private String demandeAchatReference;
    private Integer fournisseurId;
    private String fournisseurNom;
    private LocalDateTime dateCommande;
    private LocalDateTime dateLivraisonPrevue;
    private String statut;
    private BigDecimal montantTotal;
    private Integer utilisateurId;
    private String utilisateurNom;
    private List<LigneBonCommandeDto> lignes;

    // Getters and Setters
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

    public Integer getDemandeAchatId() {
        return demandeAchatId;
    }

    public void setDemandeAchatId(Integer demandeAchatId) {
        this.demandeAchatId = demandeAchatId;
    }

    public String getDemandeAchatReference() {
        return demandeAchatReference;
    }

    public void setDemandeAchatReference(String demandeAchatReference) {
        this.demandeAchatReference = demandeAchatReference;
    }

    public Integer getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Integer fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public String getFournisseurNom() {
        return fournisseurNom;
    }

    public void setFournisseurNom(String fournisseurNom) {
        this.fournisseurNom = fournisseurNom;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public LocalDateTime getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }

    public void setDateLivraisonPrevue(LocalDateTime dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public String getUtilisateurNom() {
        return utilisateurNom;
    }

    public void setUtilisateurNom(String utilisateurNom) {
        this.utilisateurNom = utilisateurNom;
    }

    public List<LigneBonCommandeDto> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneBonCommandeDto> lignes) {
        this.lignes = lignes;
    }
}
