package com.example.backend_spring.controllers;

import com.example.backend_spring.models.CategorieArticle;
import com.example.backend_spring.services.CategorieArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories-article")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategorieArticleController {

    private final CategorieArticleService categorieArticleService;

    @GetMapping
    public List<CategorieArticle> getAllCategories() {
        return categorieArticleService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieArticle> getCategorieById(@PathVariable int id) {
        return categorieArticleService.getCategorieById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CategorieArticle saveCategorie(@RequestBody CategorieArticle categorie) {
        return categorieArticleService.saveCategorie(categorie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable int id) {
        categorieArticleService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }
}
