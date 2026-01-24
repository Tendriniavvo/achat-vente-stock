package com.example.backend_spring.dto;

import com.example.backend_spring.models.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token; // For now, we'll just send a dummy token or user info
    private Utilisateur user;
}
