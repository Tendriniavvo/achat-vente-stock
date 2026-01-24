package com.example.backend_spring.repositories;

import com.example.backend_spring.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
    @Query("SELECT s FROM Stock s WHERE s.quantite <= s.article.stockMin")
    List<Stock> findStockAlerts();
}
