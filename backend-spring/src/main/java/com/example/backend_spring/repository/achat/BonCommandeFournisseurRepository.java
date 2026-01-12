package com.example.backend_spring.repository.achat;

import com.example.backend_spring.model.BonCommandeFournisseur;
import com.example.backend_spring.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BonCommandeFournisseurRepository extends JpaRepository<BonCommandeFournisseur, Integer> {

    Optional<BonCommandeFournisseur> findByReference(String reference);

    List<BonCommandeFournisseur> findByFournisseur(Fournisseur fournisseur);

    List<BonCommandeFournisseur> findByStatut(String statut);

    @Query("SELECT b FROM BonCommandeFournisseur b WHERE b.dateCommande BETWEEN :dateDebut AND :dateFin")
    List<BonCommandeFournisseur> findByDateCommandeBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT b FROM BonCommandeFournisseur b WHERE b.dateLivraisonPrevue < :date AND b.statut NOT IN ('recu', 'annule')")
    List<BonCommandeFournisseur> findCommandesEnRetard(LocalDateTime date);

    @Query("SELECT COUNT(b) FROM BonCommandeFournisseur b WHERE b.statut NOT IN ('recu', 'annule')")
    long countEnCours();
}
