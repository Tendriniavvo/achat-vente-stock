package com.example.backend_spring.controllers;

import com.example.backend_spring.models.Article;
import com.example.backend_spring.models.JournalAudit;
import com.example.backend_spring.models.Utilisateur;
import com.example.backend_spring.services.ArticleService;
import com.example.backend_spring.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleService articleService;
    private final UtilisateurService utilisateurService;

    @GetMapping
    public List<Article> getAllArticles(@RequestParam(required = false) Boolean active) {
        if (active != null && active) {
            return articleService.getActiveArticles();
        }
        return articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable int id) {
        return articleService.getArticleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Article> getArticleByCode(@PathVariable String code) {
        return articleService.getArticleByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{code}/history")
    public List<JournalAudit> getArticleHistory(@PathVariable String code) {
        return articleService.getArticleHistory(code);
    }

    @PostMapping
    public ResponseEntity<?> saveArticle(@RequestBody Article article, @RequestParam int utilisateurId) {
        return utilisateurService.getUtilisateurById(utilisateurId)
                .map(u -> ResponseEntity.ok(articleService.saveArticle(article, u)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable int id, @RequestParam int utilisateurId) {
        return utilisateurService.getUtilisateurById(utilisateurId)
                .map(u -> ResponseEntity.ok(articleService.toggleStatus(id, u)))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable int id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
