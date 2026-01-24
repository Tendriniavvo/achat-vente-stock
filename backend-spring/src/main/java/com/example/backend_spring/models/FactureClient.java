package com.example.backend_spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures_clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false, length = 50)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "commande_client_id")
    private CommandeClient commandeClient;

    @ManyToOne
    @JoinColumn(name = "livraison_id")
    private Livraison livraison;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "date_facture")
    private LocalDateTime dateFacture;

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(length = 50)
    private String statut = "attente"; // attente, validee, payee

    @ManyToOne
    @JoinColumn(name = "utilisateur_validation")
    private Utilisateur utilisateurValidation;
}
