package com.example.backend_spring.dto.achat;

public class GenererBCRequest {
    private Integer demandeAchatId;
    private Integer fournisseurId;
    private Integer utilisateurId;

    public GenererBCRequest() {
    }

    public GenererBCRequest(Integer demandeAchatId, Integer fournisseurId, Integer utilisateurId) {
        this.demandeAchatId = demandeAchatId;
        this.fournisseurId = fournisseurId;
        this.utilisateurId = utilisateurId;
    }

    public Integer getDemandeAchatId() {
        return demandeAchatId;
    }

    public void setDemandeAchatId(Integer demandeAchatId) {
        this.demandeAchatId = demandeAchatId;
    }

    public Integer getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Integer fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public Integer getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Integer utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
}
