package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.DevisClient;
import com.example.backend_spring.model.LigneDevis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneDevisRepository extends JpaRepository<LigneDevis, Integer> {

    List<LigneDevis> findByDevis(DevisClient devis);
}
