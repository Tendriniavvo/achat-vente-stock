package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.Client;
import com.example.backend_spring.model.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeClientRepository extends JpaRepository<CommandeClient, Integer> {

    Optional<CommandeClient> findByReference(String reference);

    List<CommandeClient> findByClient(Client client);

    List<CommandeClient> findByStatut(String statut);

    @Query("SELECT c FROM CommandeClient c WHERE c.dateCommande BETWEEN :dateDebut AND :dateFin")
    List<CommandeClient> findByDateCommandeBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT c FROM CommandeClient c WHERE c.dateLivraisonPrevue < :date AND c.statut NOT IN ('livree', 'annulee')")
    List<CommandeClient> findCommandesEnRetard(LocalDateTime date);
}
