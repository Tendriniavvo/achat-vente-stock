package com.example.backend_spring.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DemandeAchatDTO {
    private String reference;
    private Integer demandeurId;
    private List<LigneDemandeAchatDTO> lignes;

    @Data
    public static class LigneDemandeAchatDTO {
        private Integer articleId;
        private Integer quantite;
        private BigDecimal prixEstime;
    }
}
