package com.example.backend_spring.repository.achat;

import com.example.backend_spring.model.FactureFournisseur;
import com.example.backend_spring.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureFournisseurRepository extends JpaRepository<FactureFournisseur, Integer> {

    Optional<FactureFournisseur> findByReference(String reference);

    List<FactureFournisseur> findByFournisseur(Fournisseur fournisseur);

    List<FactureFournisseur> findByStatut(String statut);

    long countByStatut(String statut);

    @Query("SELECT f FROM FactureFournisseur f WHERE f.dateFacture BETWEEN :dateDebut AND :dateFin")
    List<FactureFournisseur> findByDateFactureBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT f FROM FactureFournisseur f WHERE f.statut = 'validee' ORDER BY f.dateFacture DESC")
    List<FactureFournisseur> findFacturesARegler();
}
