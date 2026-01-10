package com.example.backend_spring.repository.inventaire;

import com.example.backend_spring.model.Depot;
import com.example.backend_spring.model.Inventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventaireRepository extends JpaRepository<Inventaire, Integer> {

    Optional<Inventaire> findByReference(String reference);

    List<Inventaire> findByDepot(Depot depot);

    List<Inventaire> findByStatut(String statut);

    List<Inventaire> findByType(String type);

    @Query("SELECT i FROM Inventaire i WHERE i.dateDebut BETWEEN :dateDebut AND :dateFin")
    List<Inventaire> findByDateDebutBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
}
