package com.example.backend_spring.service.article;

import com.example.backend_spring.repository.article.UniteRepository;
import org.springframework.stereotype.Service;

@Service
public class UniteService {

    private final UniteRepository uniteRepository;

    public UniteService(UniteRepository uniteRepository) {
        this.uniteRepository = uniteRepository;
    }
}
