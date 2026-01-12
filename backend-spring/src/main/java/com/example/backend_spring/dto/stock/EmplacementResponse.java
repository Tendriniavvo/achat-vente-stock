package com.example.backend_spring.dto.stock;

import com.example.backend_spring.model.Emplacement;

public class EmplacementResponse {
    private Integer id;
    private Integer depotId;
    private String depotNom;
    private String code;
    private String description;
    private Integer capacite;

    // Constructeurs
    public EmplacementResponse() {
    }

    public EmplacementResponse(Emplacement emplacement) {
        this.id = emplacement.getId();
        this.depotId = emplacement.getDepot().getId();
        this.depotNom = emplacement.getDepot().getNom();
        this.code = emplacement.getCode();
        this.description = emplacement.getDescription();
        this.capacite = emplacement.getCapacite();
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDepotId() {
        return depotId;
    }

    public void setDepotId(Integer depotId) {
        this.depotId = depotId;
    }

    public String getDepotNom() {
        return depotNom;
    }

    public void setDepotNom(String depotNom) {
        this.depotNom = depotNom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }
}
