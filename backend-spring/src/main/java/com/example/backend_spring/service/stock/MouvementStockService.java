package com.example.backend_spring.service.stock;

import com.example.backend_spring.repository.stock.MouvementStockRepository;
import org.springframework.stereotype.Service;

@Service
public class MouvementStockService {

    private final MouvementStockRepository mouvementStockRepository;

    public MouvementStockService(MouvementStockRepository mouvementStockRepository) {
        this.mouvementStockRepository = mouvementStockRepository;
    }
}
