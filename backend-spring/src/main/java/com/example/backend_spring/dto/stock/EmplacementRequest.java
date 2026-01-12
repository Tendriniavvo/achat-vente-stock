package com.example.backend_spring.dto.stock;

public class EmplacementRequest {
    private Integer depotId;
    private String code;
    private String description;
    private Integer capacite;

    // Constructeurs
    public EmplacementRequest() {
    }

    public EmplacementRequest(Integer depotId, String code, String description, Integer capacite) {
        this.depotId = depotId;
        this.code = code;
        this.description = description;
        this.capacite = capacite;
    }

    // Getters et Setters
    public Integer getDepotId() {
        return depotId;
    }

    public void setDepotId(Integer depotId) {
        this.depotId = depotId;
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
