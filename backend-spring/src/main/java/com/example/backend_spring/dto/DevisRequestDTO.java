package com.example.backend_spring.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DevisRequestDTO {
    private int clientId;
    private int utilisateurId;
    private String notes;
    private List<LigneDevisDTO> lignes;

    @Data
    public static class LigneDevisDTO {
        private int articleId;
        private int quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal remise;
    }
}
