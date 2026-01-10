package com.example.backend_spring.repository.inventaire;

import com.example.backend_spring.model.AjustementStock;
import com.example.backend_spring.model.Article;
import com.example.backend_spring.model.Inventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AjustementStockRepository extends JpaRepository<AjustementStock, Integer> {

    Optional<AjustementStock> findByReference(String reference);

    List<AjustementStock> findByInventaire(Inventaire inventaire);

    List<AjustementStock> findByArticle(Article article);

    List<AjustementStock> findByStatut(String statut);

    @Query("SELECT a FROM AjustementStock a WHERE a.dateAjustement BETWEEN :dateDebut AND :dateFin")
    List<AjustementStock> findByDateAjustementBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT a FROM AjustementStock a WHERE a.statut = 'propose' ORDER BY a.dateAjustement ASC")
    List<AjustementStock> findAjustementsEnAttente();
}
