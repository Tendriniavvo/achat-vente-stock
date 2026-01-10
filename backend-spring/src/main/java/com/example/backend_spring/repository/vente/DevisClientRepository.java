package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.Client;
import com.example.backend_spring.model.DevisClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DevisClientRepository extends JpaRepository<DevisClient, Integer> {

    Optional<DevisClient> findByReference(String reference);

    List<DevisClient> findByClient(Client client);

    List<DevisClient> findByStatut(String statut);

    @Query("SELECT d FROM DevisClient d WHERE d.dateDevis BETWEEN :dateDebut AND :dateFin")
    List<DevisClient> findByDateDevisBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT d FROM DevisClient d WHERE d.dateValidite < :date AND d.statut = 'envoye'")
    List<DevisClient> findDevisExpires(LocalDateTime date);
}
