package com.example.backend_spring.services;

import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.repositories.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final JournalAuditService journalAuditService;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public List<Article> getActiveArticles() {
        return articleRepository.findAll().stream()
                .filter(Article::isActif)
                .toList();
    }

    public Optional<Article> getArticleById(int id) {
        return articleRepository.findById(id);
    }

    public Optional<Article> getArticleByCode(String code) {
        return articleRepository.findByCode(code);
    }

    @Transactional
    public Article saveArticle(Article article, Utilisateur utilisateur) {
        boolean isNew = article.getId() == 0;
        Article saved = articleRepository.save(article);
        
        String action = isNew ? "CRÉATION" : "MODIFICATION";
        String details = isNew ? "Création de l'article " + saved.getNom() : "Modification de l'article " + saved.getNom();
        
        journalAuditService.logAction(utilisateur, action, "ARTICLE", saved.getCode(), details);
        
        return saved;
    }

    @Transactional
    public Article toggleStatus(int id, Utilisateur utilisateur) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé"));
        
        article.setActif(!article.isActif());
        Article saved = articleRepository.save(article);
        
        String action = saved.isActif() ? "ACTIVATION" : "DÉSACTIVATION";
        journalAuditService.logAction(utilisateur, action, "ARTICLE", saved.getCode(), action + " de l'article " + saved.getNom());
        
        return saved;
    }

    public List<JournalAudit> getArticleHistory(String code) {
        return journalAuditService.getAuditsByModuleAndReference("ARTICLE", code);
    }

    @Transactional
    public void deleteArticle(int id) {
        articleRepository.deleteById(id);
    }
}
