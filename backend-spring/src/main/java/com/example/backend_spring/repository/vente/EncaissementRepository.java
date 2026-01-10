package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.Encaissement;
import com.example.backend_spring.model.FactureClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EncaissementRepository extends JpaRepository<Encaissement, Integer> {

    List<Encaissement> findByFactureClient(FactureClient factureClient);

    List<Encaissement> findByModePaiement(String modePaiement);

    @Query("SELECT e FROM Encaissement e WHERE e.dateEncaissement BETWEEN :dateDebut AND :dateFin")
    List<Encaissement> findByDateEncaissementBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
}
