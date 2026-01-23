package com.example.backend_spring.repositories;

import com.example.backend_spring.models.CategorieArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieArticleRepository extends JpaRepository<CategorieArticle, Integer> {
}
