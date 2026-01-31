package com.example.backend_spring.services;

import com.example.backend_spring.config.MistralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MistralAIService {

    private static final Logger logger = LoggerFactory.getLogger(MistralAIService.class);

    @Value("${mistral.api.key}")
    private String apiKey;

    @Value("${mistral.api.url}")
    private String apiUrl;

    @Value("${mistral.model}")
    private String model;

    @Autowired
    private RestTemplate restTemplate;

    public String analyzerGraphiqueBudget(Map<String, Object> budgetData) {
        String maskedKey = apiKey != null && apiKey.length() > 8 
            ? apiKey.substring(0, 4) + "...." + apiKey.substring(apiKey.length() - 4) 
            : "****";
        
        logger.info("Appel API Mistral - URL: '{}', Modèle: '{}', Clé: '{}'", 
            apiUrl, model, maskedKey);

        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new RuntimeException("L'URL de l'API Mistral n'est pas configurée (mistral.api.url).");
        }

        String systemPrompt = "Tu es un consultant expert en analyse budgétaire et data visualisation. " +
                "Ton ton est professionnel et tes réponses doivent être obligatoirement en français.";

        String userPrompt = construirUserPrompt(budgetData);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userPrompt));
        
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 2000);
        requestBody.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            throw new RuntimeException("Réponse vide ou invalide de l'API Mistral");
        } catch (HttpClientErrorException e) {
            String errorDetail;
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                errorDetail = "Clé API Mistral invalide ou expirée (401).";
            } else if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                errorDetail = "Quota de requêtes Mistral dépassé (429).";
            } else {
                errorDetail = "Erreur client Mistral (" + e.getStatusCode() + "): " + e.getResponseBodyAsString();
            }
            logger.error("Erreur HTTP Mistral: {}", errorDetail);
            throw new RuntimeException(errorDetail);
        } catch (ResourceAccessException e) {
            logger.error("Erreur de connexion Mistral: {}", e.getMessage());
            throw new RuntimeException("Impossible de contacter le serveur Mistral. Vérifiez votre connexion internet.");
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de l'appel Mistral: {}", e.getMessage());
            throw new RuntimeException("Une erreur inattendue est survenue: " + e.getMessage());
        }
    }

    public String chatWithAI(String userMessage, Map<String, Object> budgetData, List<Map<String, String>> history) {
        String systemPrompt = "Tu es un consultant expert en analyse budgétaire. Tu discutes avec l'utilisateur au sujet de ses données budgétaires : " + budgetData.toString() + ". " +
                "Réponds de manière concise, professionnelle et en français. Aide l'utilisateur à comprendre ses chiffres et propose des conseils stratégiques si demandé.";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        
        if (history != null) {
            messages.addAll(history);
        }
        
        messages.add(Map.of("role", "user", "content", userMessage));
        
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            throw new RuntimeException("Réponse vide ou invalide de l'API Mistral");
        } catch (Exception e) {
            logger.error("Erreur Chat Mistral: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la discussion : " + e.getMessage());
        }
    }

    private String construirUserPrompt(Map<String, Object> budgetData) {
        StringBuilder sb = new StringBuilder();
        sb.append("Voici les données du graphique circulaire de répartition budgétaire :\n");
        sb.append(budgetData.toString()).append("\n\n");
        sb.append("Fais une analyse structurée en respectant strictement les cinq sections suivantes :\n\n");
        
        sb.append("1. **Adéquation du format graphique** : Explique brièvement pourquoi un graphique en camembert est adapté à ces données (2 à 3 phrases maximum).\n");
        sb.append("2. **Répartition détaillée** : Réponds précisément à la question 'Quelle est la part budgétaire de chaque département dans le budget global ?' avec les pourcentages exacts et les montants.\n");
        sb.append("3. **Interprétation des résultats** : Classe les départements en trois catégories (Dominants, Intermédiaires, Faibles) et explique ce que cela signifie pour l'organisation.\n");
        sb.append("4. **Hypothèses d'écarts** : Propose 3 à 4 hypothèses plausibles expliquant les écarts budgétaires (nature stratégique, effectifs, projets en cours, objectifs de croissance).\n");
        sb.append("5. **Recommandations stratégiques** : Formule 3 à 5 recommandations concrètes et actionnables pour la direction (rééquilibrage, investissements, contrôles, synergies).");
        
        return sb.toString();
    }
}
