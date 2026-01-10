package com.example.backend_spring.repository.achat;

import com.example.backend_spring.model.FactureFournisseur;
import com.example.backend_spring.model.LigneFactureFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneFactureFournisseurRepository extends JpaRepository<LigneFactureFournisseur, Integer> {

    List<LigneFactureFournisseur> findByFacture(FactureFournisseur facture);
}
