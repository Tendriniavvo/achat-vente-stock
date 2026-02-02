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
        // Constantes pour la configuration du KPI Efficacité Approbation
        private static final double SEUIL_SOUS_CONTROLE = 3.0;
        private static final double FACTEUR_SCORE_EFFICACITE = 10.0;

        private final StockRepository stockRepository;
        private final MouvementStockRepository mouvementStockRepository;
        private final DepotRepository depotRepository;
        private final DemandeAchatRepository demandeAchatRepository;
        private final CommandeClientRepository commandeClientRepository;
        private final BonCommandeFournisseurRepository bonCommandeFournisseurRepository;
        private final BudgetService budgetService;
        private final LigneCommandeClientRepository ligneCommandeClientRepository;
        private final ArticleRepository articleRepository;

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
                List<Stock> allStocks = stockRepository.findAll();
                stats.put("stockAlertsCount", (int) allStocks.stream()
                                .filter(s -> s.getQuantite() <= (s.getArticle() != null
                                                && s.getArticle().getStockMin() != null ? s.getArticle().getStockMin()
                                                                : 0))
                                .count());
                stats.put("stockAlerts", allStocks.stream()
                                .filter(s -> s.getQuantite() <= (s.getArticle() != null
                                                && s.getArticle().getStockMin() != null ? s.getArticle().getStockMin()
                                                                : 0))
                                .limit(5).collect(Collectors.toList()));

                // NOUVEAUX KPI STOCKS

                // 1. Valeur Totale du Stock
                BigDecimal valeurTotaleStock = allStocks.stream()
                                .map(s -> s.getValeur() != null ? s.getValeur() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.put("valeurTotaleStock", valeurTotaleStock);
                stats.put("stockTrend", "+5.2%"); // Simulé pour l'instant

                // 2. Répartition du Stock par Dépôt
                Map<String, Map<String, BigDecimal>> repartitionParDepot = allStocks.stream()
                                .filter(s -> s.getDepot() != null && s.getArticle() != null)
                                .collect(Collectors.groupingBy(
                                                s -> s.getDepot().getNom(),
                                                Collectors.groupingBy(
                                                                s -> s.getArticle().getCategorie() != null
                                                                                ? s.getArticle().getCategorie().getNom()
                                                                                : "Sans Catégorie",
                                                                Collectors.reducing(BigDecimal.ZERO,
                                                                                s -> s.getValeur() != null
                                                                                                ? s.getValeur()
                                                                                                : BigDecimal.ZERO,
                                                                                BigDecimal::add))));
                stats.put("repartitionStockDepot", repartitionParDepot);

                // 3. Rotation des Stocks (Calculé : Ventes des 12 derniers mois / Stock Moyen)
                // Pour simplifier ici : Ventes totales / Valeur stock actuelle (si > 0)
                double rotation = 0;
                if (valeurTotaleStock.compareTo(BigDecimal.ZERO) > 0) {
                        rotation = totalMontantVentes.doubleValue() / valeurTotaleStock.doubleValue();
                }
                stats.put("rotationStock", String.format("%.1f", rotation));
                stats.put("rotationStatus", rotation >= 2.0 ? "success" : (rotation >= 1.0 ? "warning" : "danger"));

                // 4. Articles Dormants (> 30 jours sans mouvement)
                LocalDateTime sixtyDaysAgo = LocalDateTime.now().minusDays(60);

                // On récupère tous les mouvements des 60 derniers jours
                List<MouvementStock> movementsLast60Days = mouvementStockRepository.findAll().stream()
                                .filter(m -> m.getDateMouvement() != null && m.getDateMouvement().isAfter(sixtyDaysAgo))
                                .collect(Collectors.toList());

                // Liste des articles ayant bougé
                List<Integer> activeArticleIds = movementsLast60Days.stream()
                                .filter(m -> m.getArticle() != null)
                                .map(m -> m.getArticle().getId())
                                .distinct()
                                .collect(Collectors.toList());

                List<Map<String, Object>> articlesDormants = allStocks.stream()
                                .filter(s -> s.getArticle() != null
                                                && !activeArticleIds.contains(s.getArticle().getId()))
                                .map(s -> {
                                        Map<String, Object> m = new HashMap<>();
                                        m.put("reference", s.getArticle().getCode());
                                        m.put("nom", s.getArticle().getNom());
                                        m.put("quantite", s.getQuantite());
                                        BigDecimal valeur = s.getValeur() != null ? s.getValeur() : BigDecimal.ZERO;
                                        m.put("valeur", valeur);

                                        // Calculer jours inactivité réel (simulé par rapport à la date de création de
                                        // l'article si pas de mouvement du tout)
                                        m.put("joursInactivite", 60);
                                        return m;
                                })
                                .filter(m -> ((Integer) m.get("quantite")) > 0)
                                .sorted((a, b) -> ((BigDecimal) b.get("valeur"))
                                                .compareTo((BigDecimal) a.get("valeur")))
                                .limit(5)
                                .collect(Collectors.toList());
                stats.put("articlesDormants", articlesDormants);

                // KPI Budgets
                List<Budget> allBudgets = budgetService.getAllBudgets();
                BigDecimal totalBudget = allBudgets.stream()
                                .map(b -> b.getMontantDisponible() != null ? b.getMontantDisponible() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.put("totalBudgetDisponible", totalBudget);

                // 5. Consommation Budgétaire par Département (Gauge Chart)
                List<Map<String, Object>> budgetConsommation = allBudgets.stream()
                                .filter(b -> b.getDepartement() != null && b.getMontantInitial() != null
                                                && b.getMontantInitial().compareTo(BigDecimal.ZERO) > 0)
                                .map(b -> {
                                        Map<String, Object> m = new HashMap<>();
                                        m.put("departement", b.getDepartement().getNom());
                                        m.put("initial", b.getMontantInitial());
                                        m.put("consomme", b.getMontantConsomme());
                                        m.put("disponible", b.getMontantDisponible());

                                        double pourcentage = b.getMontantConsomme().doubleValue()
                                                        / b.getMontantInitial().doubleValue() * 100;
                                        m.put("pourcentage", Math.round(pourcentage * 10.0) / 10.0);
                                        return m;
                                })
                                .collect(Collectors.toList());
                stats.put("budgetConsommation", budgetConsommation);

                // Nouveaux KPI Achats pour le dashboard enrichi

                // 1. Répartition des Demandes par Statut
                Map<String, Long> parStatut = demandeAchatRepository.findAll().stream()
                                .collect(Collectors.groupingBy(
                                                d -> d.getStatut() != null ? d.getStatut() : "Inconnu",
                                                Collectors.counting()));
                stats.put("demandesParStatut", parStatut);

                // 2. Top 5 Fournisseurs par Volume d'Achat (basé sur les bons de commande
                // approuvés/envoyés)
                Map<String, BigDecimal> parFournisseur = bonCommandeFournisseurRepository.findAll().stream()
                                .filter(bc -> bc.getFournisseur() != null && bc.getMontantTotal() != null)
                                .collect(Collectors.groupingBy(
                                                bc -> bc.getFournisseur().getNom(),
                                                Collectors.reducing(BigDecimal.ZERO,
                                                                BonCommandeFournisseur::getMontantTotal,
                                                                BigDecimal::add)));

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
                                                Collectors.summingInt(LigneCommandeClient::getQuantite)));

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

                // 4. Délai Moyen d'Approbation (Calculé dynamiquement selon l'historique)
                List<DemandeAchat> approuvees = demandeAchatRepository.findAll().stream()
                                .filter(d -> {
                                        String s = d.getStatut() != null ? d.getStatut().toLowerCase() : "";
                                        return s.equals("approuvé") || s.equals("approuve") || s.equals("validee")
                                                        || s.equals("validée") ||
                                                        s.equals("disponible_en_stock") || s.equals("attente_finance")
                                                        || s.equals("fonds_confirmes") || s.equals("fonds_confirmés");
                                })
                                .filter(d -> d.getDateCreation() != null && d.getHistoriqueValidations() != null)
                                .collect(Collectors.toList());

                double totalDays = 0;
                int count = 0;

                for (DemandeAchat da : approuvees) {
                        String hist = da.getHistoriqueValidations();
                        String[] lines = hist.split("\n");
                        LocalDateTime dateValidationFinale = null;

                        for (int i = lines.length - 1; i >= 0; i--) {
                                String line = lines[i].trim();
                                if (line.isEmpty())
                                        continue;

                                String lineLower = line.toLowerCase();
                                if (lineLower.contains("passage de") &&
                                                (lineLower.contains("à approuvé") || lineLower.contains("à approuve") ||
                                                                lineLower.contains("à validee")
                                                                || lineLower.contains("à validée") ||
                                                                lineLower.contains("à disponible_en_stock")
                                                                || lineLower.contains("à attente_finance"))) {
                                        try {
                                                int bracketEnd = line.indexOf("]");
                                                int firstColon = line.indexOf(":");

                                                // Si on a le format avec [Validateur], la date est avant le [
                                                String datePart = "";
                                                if (line.contains("[")) {
                                                        datePart = line.substring(0, line.indexOf("[")).trim();
                                                } else if (firstColon > 0) {
                                                        // Sinon on prend ce qui est avant le premier :
                                                        datePart = line.substring(0, firstColon).trim();
                                                }

                                                if (!datePart.isEmpty()) {
                                                        dateValidationFinale = LocalDateTime.parse(datePart);
                                                        break;
                                                }
                                        } catch (Exception e) {
                                                System.err.println("Erreur parsing date historique: " + e.getMessage());
                                        }
                                }
                        }

                        if (dateValidationFinale != null) {
                                long diffHours = java.time.Duration.between(da.getDateCreation(), dateValidationFinale)
                                                .toHours();
                                totalDays += (diffHours / 24.0);
                                count++;
                        }
                }

                double delaiMoyen = count > 0 ? (totalDays / count) : 0;
                stats.put("delaiMoyenApprobation", String.format("%.1f jours", delaiMoyen));
                stats.put("efficaciteStatus", delaiMoyen <= SEUIL_SOUS_CONTROLE ? "Sous contrôle" : "À améliorer");
                stats.put("efficaciteColor", delaiMoyen <= SEUIL_SOUS_CONTROLE ? "success" : "danger");
                stats.put("efficaciteProgress",
                                Math.max(0, Math.min(100, 100 - (delaiMoyen * FACTEUR_SCORE_EFFICACITE))));

                // 6. Analyse des Marges par Article
                List<Map<String, Object>> articleMargins = articleRepository.findAll().stream()
                                .filter(a -> a.getPrixVente() != null
                                                && a.getPrixVente().compareTo(BigDecimal.ZERO) > 0)
                                .map(a -> {
                                        Map<String, Object> m = new HashMap<>();
                                        m.put("reference", a.getCode());
                                        m.put("nom", a.getNom());
                                        m.put("prixVente", a.getPrixVente());

                                        BigDecimal achat = a.getPrixAchat() != null ? a.getPrixAchat()
                                                        : BigDecimal.ZERO;
                                        BigDecimal margeBruteVal = a.getPrixVente().subtract(achat);
                                        double margePourcentage = (margeBruteVal.doubleValue()
                                                        / a.getPrixVente().doubleValue())
                                                        * 100;

                                        m.put("margeBrute", Math.round(margePourcentage * 10.0) / 10.0);
                                        m.put("methode", a.getMethodeValorisation());
                                        return m;
                                })
                                .sorted((a, b) -> Double.compare((double) b.get("margeBrute"),
                                                (double) a.get("margeBrute")))
                                .limit(10) // Top 10 articles par marge
                                .collect(Collectors.toList());
                stats.put("articleMargins", articleMargins);

                // 7. Répartition des Méthodes de Valorisation
                Map<String, Long> repartitionMethode = articleRepository.findAll().stream()
                                .collect(Collectors.groupingBy(
                                                a -> a.getMethodeValorisation() != null ? a.getMethodeValorisation()
                                                                : "Non défini",
                                                Collectors.counting()));
                stats.put("repartitionMethodeValorisation", repartitionMethode);

                return stats;
        }

        public Map<String, Object> getAchatDashboardStats() {
                Map<String, Object> stats = new HashMap<>();

                // KPI Achats
                stats.put("totalDemandesAchat", demandeAchatRepository.count());
                stats.put("demandesEnAttente", demandeAchatRepository.findAll().stream()
                                .filter(d -> {
                                        String s = d.getStatut() != null ? d.getStatut().toLowerCase() : "";
                                        return s.contains("attente") || s.contains("soumise");
                                })
                                .count());

                // KPI Budgets (relevant for purchases)
                List<Budget> allBudgets = budgetService.getAllBudgets();
                BigDecimal totalBudget = allBudgets.stream()
                                .map(b -> b.getMontantDisponible() != null ? b.getMontantDisponible() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.put("totalBudgetDisponible", totalBudget);

                // Consommation Budgétaire
                List<Map<String, Object>> budgetConsommation = allBudgets.stream()
                                .filter(b -> b.getDepartement() != null && b.getMontantInitial() != null
                                                && b.getMontantInitial().compareTo(BigDecimal.ZERO) > 0)
                                .map(b -> {
                                        Map<String, Object> m = new HashMap<>();
                                        m.put("departement", b.getDepartement().getNom());
                                        m.put("initial", b.getMontantInitial());
                                        m.put("consomme", b.getMontantConsomme());
                                        m.put("disponible", b.getMontantDisponible());

                                        double pourcentage = b.getMontantConsomme().doubleValue()
                                                        / b.getMontantInitial().doubleValue() * 100;
                                        m.put("pourcentage", Math.round(pourcentage * 10.0) / 10.0);
                                        return m;
                                })
                                .collect(Collectors.toList());
                stats.put("budgetConsommation", budgetConsommation);

                // Répartition des Demandes par Statut
                Map<String, Long> parStatut = demandeAchatRepository.findAll().stream()
                                .collect(Collectors.groupingBy(
                                                d -> d.getStatut() != null ? d.getStatut() : "Inconnu",
                                                Collectors.counting()));
                stats.put("demandesParStatut", parStatut);

                // Top 5 Fournisseurs
                Map<String, BigDecimal> parFournisseur = bonCommandeFournisseurRepository.findAll().stream()
                                .filter(bc -> bc.getFournisseur() != null && bc.getMontantTotal() != null)
                                .collect(Collectors.groupingBy(
                                                bc -> bc.getFournisseur().getNom(),
                                                Collectors.reducing(BigDecimal.ZERO,
                                                                BonCommandeFournisseur::getMontantTotal,
                                                                BigDecimal::add)));

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

                // Délai Moyen d'Approbation
                // (Reusing the logic from getDashboardStats but simplified if needed)
                // I'll copy the existing logic for consistency
                List<DemandeAchat> approuvees = demandeAchatRepository.findAll().stream()
                                .filter(d -> {
                                        String s = d.getStatut() != null ? d.getStatut().toLowerCase() : "";
                                        return s.equals("approuvé") || s.equals("approuve") || s.equals("validee")
                                                        || s.equals("validée") ||
                                                        s.equals("disponible_en_stock") || s.equals("attente_finance")
                                                        || s.equals("fonds_confirmes") || s.equals("fonds_confirmés");
                                })
                                .filter(d -> d.getDateCreation() != null && d.getHistoriqueValidations() != null)
                                .collect(Collectors.toList());

                double totalDays = 0;
                int count = 0;

                for (DemandeAchat da : approuvees) {
                        String hist = da.getHistoriqueValidations();
                        if (hist == null)
                                continue;
                        String[] lines = hist.split("\n");
                        LocalDateTime dateValidationFinale = null;

                        for (int i = lines.length - 1; i >= 0; i--) {
                                String line = lines[i].trim();
                                if (line.isEmpty())
                                        continue;

                                String lineLower = line.toLowerCase();
                                if (lineLower.contains("passage de") &&
                                                (lineLower.contains("à approuvé") || lineLower.contains("à approuve") ||
                                                                lineLower.contains("à validee")
                                                                || lineLower.contains("à validée") ||
                                                                lineLower.contains("à disponible_en_stock")
                                                                || lineLower.contains("à attente_finance"))) {
                                        try {
                                                String datePart = "";
                                                if (line.contains("[")) {
                                                        datePart = line.substring(0, line.indexOf("[")).trim();
                                                } else {
                                                        int firstColon = line.indexOf(":");
                                                        if (firstColon > 0)
                                                                datePart = line.substring(0, firstColon).trim();
                                                }

                                                if (!datePart.isEmpty()) {
                                                        dateValidationFinale = LocalDateTime.parse(datePart);
                                                        break;
                                                }
                                        } catch (Exception e) {
                                        }
                                }
                        }

                        if (dateValidationFinale != null) {
                                long diffHours = java.time.Duration.between(da.getDateCreation(), dateValidationFinale)
                                                .toHours();
                                totalDays += (diffHours / 24.0);
                                count++;
                        }
                }

                double delaiMoyen = count > 0 ? (totalDays / count) : 0;
                stats.put("delaiMoyenApprobation", String.format("%.1f jours", delaiMoyen));
                stats.put("efficaciteStatus", delaiMoyen <= SEUIL_SOUS_CONTROLE ? "Sous contrôle" : "À améliorer");
                stats.put("efficaciteColor", delaiMoyen <= SEUIL_SOUS_CONTROLE ? "success" : "danger");

                // Stock Alerts (relevant for purchase planning)
                List<Stock> allStocks = stockRepository.findAll();
                stats.put("stockAlertsCount", (int) allStocks.stream()
                                .filter(s -> s.getQuantite() <= (s.getArticle() != null
                                                && s.getArticle().getStockMin() != null ? s.getArticle().getStockMin()
                                                                : 0))
                                .count());
                stats.put("stockAlerts", allStocks.stream()
                                .filter(s -> s.getQuantite() <= (s.getArticle() != null
                                                && s.getArticle().getStockMin() != null ? s.getArticle().getStockMin()
                                                                : 0))
                                .limit(5).collect(Collectors.toList()));

                return stats;
        }

        public Map<String, Object> getVenteDashboardStats() {
                Map<String, Object> stats = new HashMap<>();

                // KPI Ventes
                stats.put("totalVentes", commandeClientRepository.count());
                BigDecimal totalMontantVentes = commandeClientRepository.findAll().stream()
                                .map(c -> c.getMontantTotal() != null ? c.getMontantTotal() : BigDecimal.ZERO)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.put("montantTotalVentes", totalMontantVentes);

                // Top 5 Articles les plus vendus
                Map<String, Integer> ventesParArticle = ligneCommandeClientRepository.findAll().stream()
                                .filter(l -> l.getArticle() != null)
                                .collect(Collectors.groupingBy(
                                                l -> l.getArticle().getNom(),
                                                Collectors.summingInt(LigneCommandeClient::getQuantite)));

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

                // Analyse des Marges par Article
                List<Map<String, Object>> articleMargins = articleRepository.findAll().stream()
                                .filter(a -> a.getPrixVente() != null
                                                && a.getPrixVente().compareTo(BigDecimal.ZERO) > 0)
                                .map(a -> {
                                        Map<String, Object> m = new HashMap<>();
                                        m.put("reference", a.getCode());
                                        m.put("nom", a.getNom());
                                        m.put("prixVente", a.getPrixVente());

                                        BigDecimal achat = a.getPrixAchat() != null ? a.getPrixAchat()
                                                        : BigDecimal.ZERO;
                                        BigDecimal margeBruteVal = a.getPrixVente().subtract(achat);
                                        double margePourcentage = (margeBruteVal.doubleValue()
                                                        / a.getPrixVente().doubleValue())
                                                        * 100;

                                        m.put("margeBrute", Math.round(margePourcentage * 10.0) / 10.0);
                                        m.put("methode", a.getMethodeValorisation());
                                        return m;
                                })
                                .sorted((a, b) -> Double.compare((double) b.get("margeBrute"),
                                                (double) a.get("margeBrute")))
                                .limit(10)
                                .collect(Collectors.toList());
                stats.put("articleMargins", articleMargins);

                // Ventes par mois (6 derniers mois)
                LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
                List<CommandeClient> recentSales = commandeClientRepository.findAll().stream()
                                .filter(c -> c.getDateCommande() != null && c.getDateCommande().isAfter(sixMonthsAgo))
                                .collect(Collectors.toList());

                stats.put("salesTrend", groupAndSumByMonthGeneric(recentSales,
                                CommandeClient::getDateCommande,
                                CommandeClient::getMontantTotal));

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
                List<Map<String, Object>> salesByMonth = groupAndSumByMonthGeneric(recentSales,
                                CommandeClient::getDateCommande,
                                CommandeClient::getMontantTotal);
                List<Map<String, Object>> purchasesByMonth = groupAndSumByMonthGeneric(recentPurchases,
                                BonCommandeFournisseur::getDateCommande, BonCommandeFournisseur::getMontantTotal);

                data.put("sales", salesByMonth);
                data.put("purchases", purchasesByMonth);

                return data;
        }

        private <T> List<Map<String, Object>> groupAndSumByMonthGeneric(List<T> items,
                        Function<T, LocalDateTime> dateExtractor,
                        Function<T, BigDecimal> amountExtractor) {
                String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                                "Dec" };

                Map<Integer, BigDecimal> monthlyTotals = items.stream()
                                .collect(Collectors.groupingBy(
                                                item -> dateExtractor.apply(item).getMonthValue(),
                                                Collectors.mapping(
                                                                item -> amountExtractor.apply(item) != null
                                                                                ? amountExtractor.apply(item)
                                                                                : BigDecimal.ZERO,
                                                                Collectors.reducing(BigDecimal.ZERO,
                                                                                BigDecimal::add))));

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
