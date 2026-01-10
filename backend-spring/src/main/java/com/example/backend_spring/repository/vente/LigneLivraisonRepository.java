package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.LigneLivraison;
import com.example.backend_spring.model.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneLivraisonRepository extends JpaRepository<LigneLivraison, Integer> {

    List<LigneLivraison> findByLivraison(Livraison livraison);
}
