package com.example.backend_spring.dto.achat;

import java.util.List;

public class DemandeAchatRequest {
    private String reference;
    private Integer demandeurId;
    private List<LigneDemandeAchatDto> lignes;

    // Constructeurs
    public DemandeAchatRequest() {
    }

    public DemandeAchatRequest(String reference, Integer demandeurId, List<LigneDemandeAchatDto> lignes) {
        this.reference = reference;
        this.demandeurId = demandeurId;
        this.lignes = lignes;
    }

    // Getters et Setters
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

    public List<LigneDemandeAchatDto> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneDemandeAchatDto> lignes) {
        this.lignes = lignes;
    }
}
