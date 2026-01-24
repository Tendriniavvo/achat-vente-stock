package com.example.backend_spring.dto;

import lombok.Data;

@Data
public class TransformationRequest {
    private int demandeAchatId;
    private int acheteurId;
    private Integer fournisseurId;
}
