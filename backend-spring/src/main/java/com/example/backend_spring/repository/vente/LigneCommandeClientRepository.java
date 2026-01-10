package com.example.backend_spring.repository.vente;

import com.example.backend_spring.model.CommandeClient;
import com.example.backend_spring.model.LigneCommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeClientRepository extends JpaRepository<LigneCommandeClient, Integer> {

    List<LigneCommandeClient> findByCommande(CommandeClient commande);
}
