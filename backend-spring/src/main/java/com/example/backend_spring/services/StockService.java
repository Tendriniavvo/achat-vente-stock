package com.example.backend_spring.services;

import com.example.backend_spring.models.Stock;
import com.example.backend_spring.models.Article;
import com.example.backend_spring.repositories.ArticleRepository;
import com.example.backend_spring.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ArticleRepository articleRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockById(int id) {
        return stockRepository.findById(id);
    }

    @Transactional
    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Transactional
    public void deleteStock(int id) {
        stockRepository.deleteById(id);
    }

    public List<Stock> getStockAlerts() {
        return stockRepository.findStockAlerts();
    }

    /**
     * Calcule le coût unitaire selon la méthode de valorisation de l'article (FIFO, LIFO, CUMP)
     */
    public BigDecimal calculerCoutUnitaire(int articleId, int depotId, Integer emplacementId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article non trouvé"));

        List<Stock> stocks = getStockDetails(articleId, depotId, emplacementId);

        if (stocks.isEmpty()) {
            return article.getPrixAchat() != null ? article.getPrixAchat() : BigDecimal.ZERO;
        }

        String methode = article.getMethodeValorisation() != null ? article.getMethodeValorisation().toUpperCase() : "CUMP";

        return switch (methode) {
            case "FIFO" -> stocks.stream()
                    .min(Comparator.comparing(Stock::getDateMaj))
                    .map(Stock::getCoutUnitaire)
                    .orElse(BigDecimal.ZERO);

            case "LIFO" -> stocks.stream()
                    .max(Comparator.comparing(Stock::getDateMaj))
                    .map(Stock::getCoutUnitaire)
                    .orElse(BigDecimal.ZERO);

            case "CUMP" -> {
                BigDecimal valeurTotale = stocks.stream()
                        .map(Stock::getValeur)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                int quantiteTotale = stocks.stream()
                        .mapToInt(Stock::getQuantite)
                        .sum();
                yield quantiteTotale > 0 
                    ? valeurTotale.divide(BigDecimal.valueOf(quantiteTotale), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            }

            default -> article.getPrixAchat() != null ? article.getPrixAchat() : BigDecimal.ZERO;
        };
    }

    public List<Stock> getStockDetails(int articleId, int depotId, Integer emplacementId) {
        return stockRepository.findByArticleId(articleId).stream()
                .filter(s -> s.getDepot() != null && s.getDepot().getId() == depotId)
                .filter(s -> {
                    if (emplacementId == null) {
                        return true; // Si pas d'emplacement spécifié, on prend tout le dépôt
                    }
                    return s.getEmplacement() != null && s.getEmplacement().getId() == (int) emplacementId;
                })
                .filter(s -> s.getQuantite() > 0)
                .toList();
    }
}
