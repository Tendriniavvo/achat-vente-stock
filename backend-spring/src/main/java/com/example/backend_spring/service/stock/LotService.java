package com.example.backend_spring.service.stock;

import com.example.backend_spring.repository.stock.LotRepository;
import org.springframework.stereotype.Service;

@Service
public class LotService {

    private final LotRepository lotRepository;

    public LotService(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }
}
