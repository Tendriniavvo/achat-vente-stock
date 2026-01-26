package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.Depot;
import com.example.backend_spring.models.Emplacement;
import com.example.backend_spring.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    @Query("SELECT s FROM Stock s WHERE s.quantite <= s.article.stockMin")
    List<Stock> findStockAlerts();

    Optional<Stock> findByArticleIdAndDepotId(int articleId, int depotId);
    
    List<Stock> findByArticleId(int articleId);
    Optional<Stock> findByArticleAndDepotAndEmplacement(Article article, Depot depot, Emplacement emplacement);
    Optional<Stock> findByArticleAndDepotAndEmplacementAndCoutUnitaire(Article article, Depot depot, Emplacement emplacement, java.math.BigDecimal coutUnitaire);

    List<Stock> findByDepot(Depot depot);
}
