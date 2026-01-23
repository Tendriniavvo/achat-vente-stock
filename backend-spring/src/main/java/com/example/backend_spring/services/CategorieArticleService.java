package com.example.backend_spring.services;

import com.example.backend_spring.models.CategorieArticle;
import com.example.backend_spring.repositories.CategorieArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategorieArticleService {
    private final CategorieArticleRepository categorieArticleRepository;

    public List<CategorieArticle> getAllCategories() {
        return categorieArticleRepository.findAll();
    }

    public Optional<CategorieArticle> getCategorieById(int id) {
        return categorieArticleRepository.findById(id);
    }

    @Transactional
    public CategorieArticle saveCategorie(CategorieArticle categorie) {
        return categorieArticleRepository.save(categorie);
    }

    @Transactional
    public void deleteCategorie(int id) {
        categorieArticleRepository.deleteById(id);
    }
}
