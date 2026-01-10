package com.example.backend_spring.repository.achat;

import com.example.backend_spring.model.BonCommandeFournisseur;
import com.example.backend_spring.model.LigneBonCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneBonCommandeRepository extends JpaRepository<LigneBonCommande, Integer> {

    List<LigneBonCommande> findByBonCommande(BonCommandeFournisseur bonCommande);
}
