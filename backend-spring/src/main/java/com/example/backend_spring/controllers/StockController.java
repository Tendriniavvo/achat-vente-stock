package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Stock;
import com.example.backend_spring.services.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StockController {

    private final StockService stockService;

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/alerts")
    public List<Stock> getStockAlerts() {
        return stockService.getStockAlerts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable int id) {
        return stockService.getStockById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Stock saveStock(@RequestBody Stock stock) {
        return stockService.saveStock(stock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable int id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/calculer-cout-unitaire")
    public ResponseEntity<java.math.BigDecimal> calculerCoutUnitaire(
            @RequestParam int articleId,
            @RequestParam int depotId,
            @RequestParam(required = false) Integer emplacementId) {
        return ResponseEntity.ok(stockService.calculerCoutUnitaire(articleId, depotId, emplacementId));
    }

    @GetMapping("/details")
    public ResponseEntity<List<Stock>> getStockDetails(
            @RequestParam int articleId,
            @RequestParam int depotId,
            @RequestParam(required = false) Integer emplacementId) {
        return ResponseEntity.ok(stockService.getStockDetails(articleId, depotId, emplacementId));
    }
}
