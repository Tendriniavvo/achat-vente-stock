package com.example.backend_spring.repository.stock;

import com.example.backend_spring.model.Article;
import com.example.backend_spring.model.Depot;
import com.example.backend_spring.model.MouvementStock;
import com.example.backend_spring.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Integer> {

    List<MouvementStock> findByArticle(Article article);

    List<MouvementStock> findByDepot(Depot depot);

    List<MouvementStock> findByType(String type);

    List<MouvementStock> findByUtilisateur(Utilisateur utilisateur);

    @Query("SELECT m FROM MouvementStock m WHERE m.dateMouvement BETWEEN :dateDebut AND :dateFin")
    List<MouvementStock> findByDateMouvementBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    @Query("SELECT m FROM MouvementStock m WHERE m.article = :article AND m.dateMouvement BETWEEN :dateDebut AND :dateFin ORDER BY m.dateMouvement DESC")
    List<MouvementStock> findHistoriqueArticle(Article article, LocalDateTime dateDebut, LocalDateTime dateFin);
}
