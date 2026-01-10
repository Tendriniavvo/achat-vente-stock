package com.example.backend_spring.repository.article;

import com.example.backend_spring.model.Article;
import com.example.backend_spring.model.CategorieArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

    Optional<Article> findByCode(String code);

    List<Article> findByNomContainingIgnoreCase(String nom);

    List<Article> findByCategorie(CategorieArticle categorie);

    List<Article> findByActif(Boolean actif);

    @Query("SELECT a FROM Article a WHERE a.traceableLot = true")
    List<Article> findArticlesTraceables();
}
