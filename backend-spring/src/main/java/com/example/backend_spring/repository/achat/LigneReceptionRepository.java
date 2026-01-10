package com.example.backend_spring.repository.achat;

import com.example.backend_spring.model.LigneReception;
import com.example.backend_spring.model.Reception;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneReceptionRepository extends JpaRepository<LigneReception, Integer> {

    List<LigneReception> findByReception(Reception reception);
}
