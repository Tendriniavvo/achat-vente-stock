package com.example.backend_spring.controller.article;

import com.example.backend_spring.service.article.CategorieArticleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories-articles")
@CrossOrigin(origins = "*")
public class CategorieArticleController {

    private final CategorieArticleService categorieArticleService;

    public CategorieArticleController(CategorieArticleService categorieArticleService) {
        this.categorieArticleService = categorieArticleService;
    }
}
