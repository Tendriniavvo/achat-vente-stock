package com.example.backend_spring.service.article;

import com.example.backend_spring.repository.article.CategorieArticleRepository;
import org.springframework.stereotype.Service;

@Service
public class CategorieArticleService {

    private final CategorieArticleRepository categorieArticleRepository;

    public CategorieArticleService(CategorieArticleRepository categorieArticleRepository) {
        this.categorieArticleRepository = categorieArticleRepository;
    }
}
