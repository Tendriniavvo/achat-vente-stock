package com.example.backend_spring.services;

import com.example.backend_spring.models.Stock;
import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.Budget;
import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.repositories.StockRepository;
import com.example.backend_spring.repositories.ArticleRepository;
import com.example.backend_spring.repositories.BudgetRepository;
import com.example.backend_spring.repositories.CommandeClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GlobalAIService {

        private final MistralAIService mistralAIService;
        private final StockRepository stockRepository;
        private final ArticleRepository articleRepository;
        private final BudgetRepository budgetRepository;
        private final CommandeClientRepository commandeClientRepository;

        public String analyzeMarginsAndValuations() {
                // 1. Collecter les données pour l'analyse
                List<Article> articles = articleRepository.findAll();
                List<Map<String, Object>> articleSummary = articles.stream()
                                .filter(a -> a.getPrixVente() != null && a.getPrixVente().doubleValue() > 0)
                                .map(a -> {
                                        Map<String, Object> m = new HashMap<>();
                                        m.put("nom", a.getNom());
                                        m.put("reference", a.getCode());
                                        m.put("prixVente", a.getPrixVente());
                                        m.put("prixAchat", a.getPrixAchat() != null ? a.getPrixAchat() : 0);
                                        m.put("methode", a.getMethodeValorisation());

                                        double marge = a.getPrixVente()
                                                        .subtract(a.getPrixAchat() != null ? a.getPrixAchat()
                                                                        : java.math.BigDecimal.ZERO)
                                                        .doubleValue();
                                        double margePourcentage = (marge / a.getPrixVente().doubleValue()) * 100;
                                        m.put("margePourcentage", Math.round(margePourcentage * 10.0) / 10.0);

                                        return m;
                                })
                                .sorted((a, b) -> Double.compare((double) b.get("margePourcentage"),
                                                (double) a.get("margePourcentage")))
                                .limit(20)
                                .collect(Collectors.toList());

                // Calculer la répartition des méthodes de valorisation
                Map<String, Long> repartitionMethode = articles.stream()
                                .collect(Collectors.groupingBy(
                                                a -> a.getMethodeValorisation() != null ? a.getMethodeValorisation()
                                                                : "Non défini",
                                                Collectors.counting()));

                // 2. Préparer le prompt structuré
                String systemPrompt = "Tu es un expert en analyse financière et gestion de stock.\n\n" +
                                "DONNÉES RÉELLES DU SYSTÈME :\n" +
                                "- Top 20 articles par marge : " + articleSummary.toString() + "\n" +
                                "- Répartition des méthodes de valorisation : " + repartitionMethode.toString() + "\n\n"
                                +
                                "TON RÔLE :\n" +
                                "Effectuer une analyse structurée en 5 POINTS PRÉCIS, en imitant exactement le style d'une analyse de distribution (comme une enquête de satisfaction).\n\n"
                                +
                                "STRUCTURE OBLIGATOIRE ET STYLE DE RÉPONSE :\n\n" +
                                "1. **Pourquoi on a choisi ce graphique (histogramme/camembert)**\n" +
                                "   Explique que l'histogramme est le plus adapté pour analyser la distribution des marges sur un ensemble d'articles. L'objectif est de voir comment ces marges sont distribuées : y a-t-il beaucoup de marges faibles, moyennes ou élevées ? Il permet de repérer les tendances, la concentration et l'asymétrie.\n"
                                +
                                "   Pour le camembert, explique qu'il permet de voir instantanément la méthode dominante et les proportions relatives des méthodes de valorisation.\n\n"
                                +
                                "2. **Question à laquelle le graphique répond**\n" +
                                "   Formule la question sous cette forme : « Comment se répartissent les niveaux de rentabilité des articles et y a-t-il plutôt des articles à forte ou faible marge ? »\n"
                                +
                                "   En d'autres mots : les marges sont-elles majoritairement élevées, moyennes ou faibles ?\n\n"
                                +
                                "3. **Réponse à la question (interprétation générale du graphe)**\n" +
                                "   L'histogramme montre le nombre d'articles pour chaque tranche de marge. En observant la forme de la distribution (basée sur les DONNÉES RÉELLES fournies) :\n"
                                +
                                "   - Si les marges sont concentrées vers le haut (ex: >25%) -> Rentabilité globale satisfaisante.\n"
                                +
                                "   - Si la distribution est centrée -> Rentabilité moyenne.\n" +
                                "   - Si beaucoup de marges sont faibles -> Problème important de rentabilité.\n" +
                                "   (Cite ici les données réelles : Top 3 des articles, articles les moins rentables, et méthode dominante).\n\n"
                                +
                                "4. **Ce qui peut expliquer la forme de la distribution (hypothèses)**\n" +
                                "   Propose des raisons possibles comme :\n" +
                                "   - **Qualité du produit / service** : Un produit fiable et un bon service client tirent les prix et les marges vers le haut.\n"
                                +
                                "   - **Expérience client globale** : Délais de livraison, accueil, support après-vente influencent la capacité à maintenir des marges élevées.\n"
                                +
                                "   - **Attentes des clients** : Si les attentes sont élevées, une performance correcte peut générer des marges moyennes.\n"
                                +
                                "   - **Problèmes ponctuels** : Incidents, retards, bugs ou ruptures de stock peuvent créer une 'bosse' de marges basses.\n\n"
                                +
                                "5. **Décisions et recommandations possibles**\n" +
                                "   Tu DOIS fournir des recommandations basées sur les données réelles :\n" +
                                "   - **Si la rentabilité est élevée** : Maintenir les points forts identifiés et communiquer dessus pour renforcer l'image de marque.\n"
                                +
                                "   - **Si la rentabilité est moyenne** : Analyser les causes des marges moyennes (commentaires, enquêtes qualitatives) et mettre en place un plan d'amélioration (formation du personnel, amélioration du produit, clarification des promesses marketing).\n"
                                +
                                "   - **Si la rentabilité est faible** : Traiter en priorité les problèmes récurrents (retards, qualité, service client), lancer un plan d'action correctif et suivre l'évolution des notes dans le temps.\n"
                                +
                                "   - **Dans tous les cas** : Répéter régulièrement l'analyse pour mesurer l'impact des actions menées et suivre la rentabilité comme un indicateur clé (KPI).\n\n"
                                +
                                "CONSIGNES CRITIQUES :\n" +
                                "- RÉPONDS IMPÉRATIVEMENT AUX 5 POINTS CI-DESSUS SANS EXCEPTION.\n" +
                                "- NE COUPE PAS TA RÉPONSE AVANT D'AVOIR FINI LE POINT 5.\n" +
                                "- Réponds en français au format Markdown.\n" +
                                "- Utilise les données réelles pour nourrir l'analyse du point 3.\n" +
                                "- Garde exactement ce ton pédagogique et structuré.";

                String userMessage = "Peux-tu analyser les graphiques de marges et de valorisation de mon tableau de bord selon la structure en 5 points ?";

                return mistralAIService.callMistral(systemPrompt, userMessage, new ArrayList<>());
        }

        public String askGlobalQuestion(String userMessage, List<Map<String, String>> history) {
                // 1. Collecter les données de contexte
                Map<String, Object> contextData = new HashMap<>();

                // Données de stock
                List<Stock> stocks = stockRepository.findAll();
                List<Map<String, Object>> stockSummary = stocks.stream()
                                .filter(s -> s.getQuantite() > 0)
                                .map(s -> {
                                        Map<String, Object> m = new HashMap<>();
                                        m.put("article", s.getArticle().getNom());
                                        m.put("reference", s.getArticle().getCode());
                                        m.put("quantite", s.getQuantite());
                                        m.put("depot", s.getDepot().getNom());
                                        m.put("emplacement", s.getEmplacement() != null ? s.getEmplacement().getCode()
                                                        : "N/A");
                                        return m;
                                })
                                .collect(Collectors.toList());
                contextData.put("stocks", stockSummary);

                // Données de budget
                List<Budget> budgets = budgetRepository.findAll();
                List<Map<String, Object>> budgetSummary = budgets.stream()
                                .map(b -> {
                                        Map<String, Object> m = new HashMap<>();
                                        m.put("departement", b.getDepartement().getNom());
                                        m.put("annee", b.getAnnee());
                                        m.put("initial", b.getMontantInitial());
                                        m.put("consomme", b.getMontantConsomme());
                                        m.put("disponible", b.getMontantDisponible());
                                        return m;
                                })
                                .collect(Collectors.toList());
                contextData.put("budgets", budgetSummary);

                // 2. Préparer le prompt système pour le chatbot global
                String systemPrompt = "Tu es l'assistant intelligent de l'application 'Gestion Achat-Vente-Stock'. " +
                                "Tu as accès aux données en temps réel de l'entreprise.\n\n" +
                                "CONTEXTE ACTUEL :\n" +
                                "- STOCKS : " + stockSummary.toString() + "\n" +
                                "- BUDGETS : " + budgetSummary.toString() + "\n\n" +
                                "TON RÔLE :\n" +
                                "1. Répondre aux questions précises sur les stocks (ex: 'Quel est le stock pour ART-001 ?').\n"
                                +
                                "2. Informer sur les budgets restants par département.\n" +
                                "3. Aider les utilisateurs à comprendre l'état de l'entreprise.\n" +
                                "4. Si une donnée n'est pas dans le contexte, explique poliment que tu n'as pas accès à cette information spécifique.\n\n"
                                +
                                "CONSIGNES :\n" +
                                "- Réponds de manière concise et professionnelle.\n" +
                                "- Utilise toujours le français.\n" +
                                "- Formate tes réponses avec du Markdown pour la lisibilité (tableaux, listes, gras).";

                // 3. Appeler le service Mistral
                return mistralAIService.callMistral(systemPrompt, userMessage, history);
        }
}
