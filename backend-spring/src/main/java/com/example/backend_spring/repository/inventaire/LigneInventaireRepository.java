package com.example.backend_spring.repository.inventaire;

import com.example.backend_spring.model.Inventaire;
import com.example.backend_spring.model.LigneInventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneInventaireRepository extends JpaRepository<LigneInventaire, Integer> {

    List<LigneInventaire> findByInventaire(Inventaire inventaire);

    @Query("SELECT l FROM LigneInventaire l WHERE l.inventaire = :inventaire AND l.ecart <> 0")
    List<LigneInventaire> findLignesAvecEcart(Inventaire inventaire);
}
