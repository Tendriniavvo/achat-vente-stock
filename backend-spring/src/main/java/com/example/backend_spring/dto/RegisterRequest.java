package com.example.backend_spring.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Set<Integer> roleIds;
    private Integer departementId;
}
