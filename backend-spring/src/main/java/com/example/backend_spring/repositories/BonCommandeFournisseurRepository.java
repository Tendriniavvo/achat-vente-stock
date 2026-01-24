package com.example.backend_spring.repositories;

import com.example.backend_spring.models.BonCommandeFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BonCommandeFournisseurRepository extends JpaRepository<BonCommandeFournisseur, Integer> {
    Optional<BonCommandeFournisseur> findByReference(String reference);
    List<BonCommandeFournisseur> findByFournisseur_IdOrderByDateCommandeDesc(int fournisseurId);
}
