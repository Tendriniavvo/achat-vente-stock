package com.example.backend_spring.dto;

import lombok.Data;

@Data
public class StatutUpdateRequest {
    private String statut;
    private int utilisateurId;
}
