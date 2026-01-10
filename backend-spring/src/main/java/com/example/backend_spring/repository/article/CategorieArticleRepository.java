package com.example.backend_spring.repository.article;

import com.example.backend_spring.model.CategorieArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieArticleRepository extends JpaRepository<CategorieArticle, Integer> {

    Optional<CategorieArticle> findByNom(String nom);

    List<CategorieArticle> findByParent(CategorieArticle parent);

    List<CategorieArticle> findByParentIsNull();
}
