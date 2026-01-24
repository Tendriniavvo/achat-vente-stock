package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "encaissements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encaissement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "facture_client_id")
    private FactureClient factureClient;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_encaissement")
    private LocalDateTime dateEncaissement = LocalDateTime.now();

    @Column(name = "mode_paiement", length = 50)
    private String modePaiement;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
