package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.Client;
import com.example.backend_spring.model.FactureClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureClientRepository extends JpaRepository<FactureClient, Integer> {

    Optional<FactureClient> findByReference(String reference);

    List<FactureClient> findByClient(Client client);

    List<FactureClient> findByStatut(String statut);

    long countByStatut(String statut);

    @Query("SELECT f FROM FactureClient f WHERE f.dateFacture BETWEEN :dateDebut AND :dateFin")
    List<FactureClient> findByDateFactureBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT f FROM FactureClient f WHERE f.statut = 'validee' ORDER BY f.dateFacture DESC")
    List<FactureClient> findFacturesEnAttentePaiement();
}
