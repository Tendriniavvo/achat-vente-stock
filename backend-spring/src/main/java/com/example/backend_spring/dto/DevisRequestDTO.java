package com.example.backend_spring.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DevisRequestDTO {
    private Integer clientId;
    private Integer utilisateurId;
    private String notes;
    private List<LigneDevisDTO> lignes;

    @Data
    public static class LigneDevisDTO {
        private Integer articleId;
        private Integer quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal remise;
    }
}
