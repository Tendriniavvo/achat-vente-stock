package com.example.backend_spring.repository.article;

import com.example.backend_spring.model.Unite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UniteRepository extends JpaRepository<Unite, Integer> {

    Optional<Unite> findByNom(String nom);

    Optional<Unite> findBySymbole(String symbole);
}
