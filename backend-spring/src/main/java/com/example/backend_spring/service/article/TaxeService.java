package com.example.backend_spring.service.article;

import com.example.backend_spring.repository.article.TaxeRepository;
import org.springframework.stereotype.Service;

@Service
public class TaxeService {

    private final TaxeRepository taxeRepository;

    public TaxeService(TaxeRepository taxeRepository) {
        this.taxeRepository = taxeRepository;
    }
}
