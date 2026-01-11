package com.example.backend_spring.service.article;

import com.example.backend_spring.dto.article.ArticleDto;
import com.example.backend_spring.model.Article;
import com.example.backend_spring.repository.article.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public List<ArticleDto> getAllArticlesActifs() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream()
                .filter(Article::getActif)
                .map(this::toDto)
                .toList();
    }

    private ArticleDto toDto(Article article) {
        return new ArticleDto(
                article.getId(),
                article.getCode(),
                article.getNom(),
                article.getDescription(),
                article.getPrixAchat(),
                article.getPrixVente());
    }
}
