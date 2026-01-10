package com.example.backend_spring.service.stock;

import com.example.backend_spring.repository.stock.DepotRepository;
import org.springframework.stereotype.Service;

@Service
public class DepotService {

    private final DepotRepository depotRepository;

    public DepotService(DepotRepository depotRepository) {
        this.depotRepository = depotRepository;
    }
}
