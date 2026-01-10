package com.example.backend_spring.repository.achat;

import com.example.backend_spring.model.DemandeAchat;
import com.example.backend_spring.model.LigneDemandeAchat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneDemandeAchatRepository extends JpaRepository<LigneDemandeAchat, Integer> {

    List<LigneDemandeAchat> findByDemandeAchat(DemandeAchat demandeAchat);
}
