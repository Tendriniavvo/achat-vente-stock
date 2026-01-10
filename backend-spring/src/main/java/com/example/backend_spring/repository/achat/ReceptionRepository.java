package com.example.backend_spring.repository.achat;

import com.example.backend_spring.model.BonCommandeFournisseur;
import com.example.backend_spring.model.Reception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceptionRepository extends JpaRepository<Reception, Integer> {

    Optional<Reception> findByReference(String reference);

    List<Reception> findByBonCommande(BonCommandeFournisseur bonCommande);

    List<Reception> findByStatut(String statut);

    @Query("SELECT r FROM Reception r WHERE r.dateReception BETWEEN :dateDebut AND :dateFin")
    List<Reception> findByDateReceptionBetween(LocalDateTime dateDebut, LocalDateTime dateFin);
}
