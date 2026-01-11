package com.example.backend_spring.controller.article;

import com.example.backend_spring.dto.article.ArticleDto;
import com.example.backend_spring.service.article.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<List<ArticleDto>> getAllArticlesActifs() {
        List<ArticleDto> articles = articleService.getAllArticlesActifs();
        return ResponseEntity.ok(articles);
    }
}
