package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.FactureClient;
import com.example.backend_spring.model.LigneFactureClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneFactureClientRepository extends JpaRepository<LigneFactureClient, Integer> {

    List<LigneFactureClient> findByFacture(FactureClient facture);
}
