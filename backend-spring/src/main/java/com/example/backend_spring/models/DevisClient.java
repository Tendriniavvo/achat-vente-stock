package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "devis_clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevisClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "date_devis")
    private LocalDateTime dateDevis = LocalDateTime.now();

    @Column(name = "date_validite")
    private LocalDateTime dateValidite;

    @Column(length = 50)
    private String statut = "brouillon"; // brouillon, envoye, accepte, rejete

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(name = "remise_exceptionnelle")
    private boolean remiseExceptionnelle = false;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;
}
