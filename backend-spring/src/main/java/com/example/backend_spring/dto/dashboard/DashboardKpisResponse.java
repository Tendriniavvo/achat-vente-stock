package com.example.backend_spring.dto.dashboard;

import java.math.BigDecimal;

public class DashboardKpisResponse {

    private BigDecimal valeurTotaleStock;
    private Long nombreCommandesEnCours;
    private BigDecimal tauxRotationStocks;
    private Long facturesEnAttente;
    private Long facturesPayees;

    public DashboardKpisResponse() {
    }

    public DashboardKpisResponse(BigDecimal valeurTotaleStock, Long nombreCommandesEnCours, BigDecimal tauxRotationStocks,
            Long facturesEnAttente, Long facturesPayees) {
        this.valeurTotaleStock = valeurTotaleStock;
        this.nombreCommandesEnCours = nombreCommandesEnCours;
        this.tauxRotationStocks = tauxRotationStocks;
        this.facturesEnAttente = facturesEnAttente;
        this.facturesPayees = facturesPayees;
    }

    public BigDecimal getValeurTotaleStock() {
        return valeurTotaleStock;
    }

    public void setValeurTotaleStock(BigDecimal valeurTotaleStock) {
        this.valeurTotaleStock = valeurTotaleStock;
    }

    public Long getNombreCommandesEnCours() {
        return nombreCommandesEnCours;
    }

    public void setNombreCommandesEnCours(Long nombreCommandesEnCours) {
        this.nombreCommandesEnCours = nombreCommandesEnCours;
    }

    public BigDecimal getTauxRotationStocks() {
        return tauxRotationStocks;
    }

    public void setTauxRotationStocks(BigDecimal tauxRotationStocks) {
        this.tauxRotationStocks = tauxRotationStocks;
    }

    public Long getFacturesEnAttente() {
        return facturesEnAttente;
    }

    public void setFacturesEnAttente(Long facturesEnAttente) {
        this.facturesEnAttente = facturesEnAttente;
    }

    public Long getFacturesPayees() {
        return facturesPayees;
    }

    public void setFacturesPayees(Long facturesPayees) {
        this.facturesPayees = facturesPayees;
    }
}
