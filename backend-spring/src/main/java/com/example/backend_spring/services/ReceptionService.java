package com.example.backend_spring.services;

import com.example.backend_spring.models.Reception;
import com.example.backend_spring.repositories.ReceptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceptionService {
    private final ReceptionRepository receptionRepository;

    public List<Reception> getAllReceptions() {
        return receptionRepository.findAll();
    }

    public Optional<Reception> getReceptionById(int id) {
        return receptionRepository.findById(id);
    }

    public Optional<Reception> getReceptionByReference(String reference) {
        return receptionRepository.findByReference(reference);
    }

    @Transactional
    public Reception saveReception(Reception reception) {
        return receptionRepository.save(reception);
    }

    @Transactional
    public void deleteReception(int id) {
        receptionRepository.deleteById(id);
    }
}
