package com.example.backend_spring.services;

import com.example.backend_spring.models.*;
import com.example.backend_spring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    // Constantes pour la configuration du KPI Efficacité Approbation (si réutilisé plus tard)
    private static final double OFFSET_JOURS_ADMIN = 1.2; 
    private static final double SEUIL_SOUS_CONTROLE = 3.0;
    private static final double FACTEUR_SCORE_EFFICACITE = 10.0;

    private final StockService stockService;
    private final DemandeAchatRepository demandeAchatRepository;
    private final CommandeClientRepository commandeClientRepository;
    private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;
    private final BudgetService budgetService;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // KPI Achats
        stats.put("totalDemandesAchat", demandeAchatRepository.count());
        stats.put("demandesEnAttente", demandeAchatRepository.findAll().stream()
                .filter(d -> {
                    String s = d.getStatut() != null ? d.getStatut().toLowerCase() : "";
                    return s.contains("attente") || s.contains("soumise");
                })
                .count());

        // KPI Ventes
        stats.put("totalVentes", commandeClientRepository.count());
        BigDecimal totalMontantVentes = commandeClientRepository.findAll().stream()
                .map(c -> c.getMontantTotal() != null ? c.getMontantTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("montantTotalVentes", totalMontantVentes);

        // KPI Stocks
        List<Stock> alerts = stockService.getStockAlerts();
        stats.put("stockAlertsCount", alerts.size());
        stats.put("stockAlerts", alerts.stream().limit(5).collect(Collectors.toList()));

        // KPI Budgets
        BigDecimal totalBudget = budgetService.getAllBudgets().stream()
                .map(b -> b.getMontantDisponible() != null ? b.getMontantDisponible() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalBudgetDisponible", totalBudget);

        // Nouveaux KPI Achats pour le dashboard enrichi
        
        // 1. Répartition des Demandes par Statut
        Map<String, Long> parStatut = demandeAchatRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        d -> d.getStatut() != null ? d.getStatut() : "Inconnu",
                        Collectors.counting()
                ));
        stats.put("demandesParStatut", parStatut);

        // 2. Top 5 Fournisseurs par Volume d'Achat (basé sur les bons de commande approuvés/envoyés)
        Map<String, BigDecimal> parFournisseur = bonCommandeFournisseurRepository.findAll().stream()
                .filter(bc -> bc.getFournisseur() != null && bc.getMontantTotal() != null)
                .collect(Collectors.groupingBy(
                        bc -> bc.getFournisseur().getNom(),
                        Collectors.reducing(BigDecimal.ZERO, BonCommandeFournisseur::getMontantTotal, BigDecimal::add)
                ));
        
        List<Map<String, Object>> topFournisseurs = parFournisseur.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("nom", entry.getKey());
                    m.put("volume", entry.getValue());
                    return m;
                })
                .collect(Collectors.toList());
        stats.put("topFournisseurs", topFournisseurs);

        // 3. Top 5 Articles les plus vendus (Gestion des Ventes)
        Map<String, Integer> ventesParArticle = ligneCommandeClientRepository.findAll().stream()
                .filter(l -> l.getArticle() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getArticle().getNom(),
                        Collectors.summingInt(LigneCommandeClient::getQuantite)
                ));

        List<Map<String, Object>> topArticles = ventesParArticle.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("nom", entry.getKey());
                    m.put("quantite", entry.getValue());
                    return m;
                })
                .collect(Collectors.toList());
        stats.put("topArticlesVendus", topArticles);

        return stats;
    }

    public Map<String, Object> getPerformanceData() {
        Map<String, Object> data = new HashMap<>();

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);

        // Fetch real sales data
        List<CommandeClient> recentSales = commandeClientRepository.findAll().stream()
                .filter(c -> c.getDateCommande() != null && c.getDateCommande().isAfter(sixMonthsAgo))
                .collect(Collectors.toList());

        // Fetch real purchase data
        List<BonCommandeFournisseur> recentPurchases = bonCommandeFournisseurRepository.findAll().stream()
                .filter(b -> b.getDateCommande() != null && b.getDateCommande().isAfter(sixMonthsAgo))
                .collect(Collectors.toList());

        // Group and sum by month
        List<Map<String, Object>> salesByMonth = groupAndSumByMonthGeneric(recentSales, CommandeClient::getDateCommande,
                CommandeClient::getMontantTotal);
        List<Map<String, Object>> purchasesByMonth = groupAndSumByMonthGeneric(recentPurchases,
                BonCommandeFournisseur::getDateCommande, BonCommandeFournisseur::getMontantTotal);

        data.put("sales", salesByMonth);
        data.put("purchases", purchasesByMonth);

        return data;
    }

    private <T> List<Map<String, Object>> groupAndSumByMonthGeneric(List<T> items, Function<T, LocalDateTime> dateExtractor,
            Function<T, BigDecimal> amountExtractor) {
        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

        Map<Integer, BigDecimal> monthlyTotals = items.stream()
                .collect(Collectors.groupingBy(
                        item -> dateExtractor.apply(item).getMonthValue(),
                        Collectors.mapping(
                                item -> amountExtractor.apply(item) != null ? amountExtractor.apply(item) : BigDecimal.ZERO,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // S'assurer que les 6 derniers mois sont présents, même avec 0
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            LocalDateTime targetDate = now.minusMonths(i);
            int monthValue = targetDate.getMonthValue();
            Map<String, Object> m = new HashMap<>();
            m.put("month", months[monthValue - 1]);
            m.put("amount", monthlyTotals.getOrDefault(monthValue, BigDecimal.ZERO));
            result.add(m);
        }

        return result;
    }
}
