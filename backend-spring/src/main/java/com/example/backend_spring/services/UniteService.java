package com.example.backend_spring.services;

import com.example.backend_spring.models.Unite;
import com.example.backend_spring.repositories.UniteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UniteService {
    private final UniteRepository uniteRepository;

    public List<Unite> getAllUnites() {
        return uniteRepository.findAll();
    }

    public Optional<Unite> getUniteById(int id) {
        return uniteRepository.findById(id);
    }

    @Transactional
    public Unite saveUnite(Unite unite) {
        return uniteRepository.save(unite);
    }

    @Transactional
    public void deleteUnite(int id) {
        uniteRepository.deleteById(id);
    }
}
