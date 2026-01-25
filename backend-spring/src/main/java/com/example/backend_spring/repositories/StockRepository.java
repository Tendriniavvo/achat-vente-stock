package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.Depot;
import com.example.backend_spring.models.Emplacement;
import com.example.backend_spring.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    Optional<Stock> findByArticleAndDepotAndEmplacement(Article article, Depot depot, Emplacement emplacement);

    List<Stock> findByDepot(Depot depot);

    List<Stock> findByArticleId(int articleId);

    Optional<Stock> findByArticleIdAndDepotId(int articleId, int depotId);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Stock s WHERE s.quantite <= s.article.stockMin")
    List<Stock> findStockAlerts();
}
