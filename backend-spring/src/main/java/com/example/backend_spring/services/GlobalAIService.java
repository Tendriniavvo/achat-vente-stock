package com.example.backend_spring.services;

import com.example.backend_spring.models.Stock;
import com.example.backend_spring.models.Budget;
import com.example.backend_spring.models.CommandeClient;
import com.example.backend_spring.repositories.StockRepository;
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
    private final BudgetRepository budgetRepository;
    private final CommandeClientRepository commandeClientRepository;

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
                    m.put("emplacement", s.getEmplacement() != null ? s.getEmplacement().getCode() : "N/A");
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
                "1. Répondre aux questions précises sur les stocks (ex: 'Quel est le stock pour ART-001 ?').\n" +
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
