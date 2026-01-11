package com.example.backend_spring.dto.achat;

import java.time.LocalDateTime;
import java.util.List;

public class DemandeAchatResponse {
    private Integer id;
    private String reference;
    private Integer demandeurId;
    private String demandeurNom;
    private String demandeurPrenom;
    private LocalDateTime dateCreation;
    private String statut;
    private String historiqueValidations;
    private List<LigneDemandeAchatDto> lignes;

    // Constructeurs
    public DemandeAchatResponse() {
    }

    public DemandeAchatResponse(Integer id, String reference, Integer demandeurId, String demandeurNom,
            String demandeurPrenom,
            LocalDateTime dateCreation, String statut, String historiqueValidations,
            List<LigneDemandeAchatDto> lignes) {
        this.id = id;
        this.reference = reference;
        this.demandeurId = demandeurId;
        this.demandeurNom = demandeurNom;
        this.demandeurPrenom = demandeurPrenom;
        this.dateCreation = dateCreation;
        this.statut = statut;
        this.historiqueValidations = historiqueValidations;
        this.lignes = lignes;
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

    public Integer getDemandeurId() {
        return demandeurId;
    }

    public void setDemandeurId(Integer demandeurId) {
        this.demandeurId = demandeurId;
    }

    public String getDemandeurNom() {
        return demandeurNom;
    }

    public void setDemandeurNom(String demandeurNom) {
        this.demandeurNom = demandeurNom;
    }

    public String getDemandeurPrenom() {
        return demandeurPrenom;
    }

    public void setDemandeurPrenom(String demandeurPrenom) {
        this.demandeurPrenom = demandeurPrenom;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getHistoriqueValidations() {
        return historiqueValidations;
    }

    public void setHistoriqueValidations(String historiqueValidations) {
        this.historiqueValidations = historiqueValidations;
    }

    public List<LigneDemandeAchatDto> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneDemandeAchatDto> lignes) {
        this.lignes = lignes;
    }
}
