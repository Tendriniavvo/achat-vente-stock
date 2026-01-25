package com.example.backend_spring.repositories;

import com.example.backend_spring.models.LigneFactureFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneFactureFournisseurRepository extends JpaRepository<LigneFactureFournisseur, Integer> {
    List<LigneFactureFournisseur> findByFactureId(int factureId);
}
