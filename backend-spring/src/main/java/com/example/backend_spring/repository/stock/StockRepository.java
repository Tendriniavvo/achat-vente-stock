package com.example.backend_spring.repository.stock;

import com.example.backend_spring.model.Article;
import com.example.backend_spring.model.Depot;
import com.example.backend_spring.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    Optional<Stock> findByArticleAndDepot(Article article, Depot depot);

    List<Stock> findByArticle(Article article);

    List<Stock> findByDepot(Depot depot);

    @Query("SELECT s FROM Stock s WHERE s.quantite < s.article.stockMin")
    List<Stock> findStocksEnRupture();

    @Query("SELECT s FROM Stock s WHERE s.quantite > s.article.stockMax")
    List<Stock> findStocksEnSurstock();

    @Query("SELECT COALESCE(SUM(s.valeur), 0) FROM Stock s")
    BigDecimal sumValeurTotale();

    @Query("SELECT COALESCE(AVG(s.quantite), 0) FROM Stock s")
    double avgQuantite();
}
