package com.example.backend_spring.repositories;

import com.example.backend_spring.models.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Integer> {
    List<MouvementStock> findByReferenceDocumentAndType(String referenceDocument, String type);
}
