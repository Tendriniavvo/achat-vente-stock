package com.example.backend_spring.services;

import com.example.backend_spring.models.Lot;
import com.example.backend_spring.repositories.LotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LotService {
    private final LotRepository lotRepository;

    public List<Lot> getAllLots() {
        return lotRepository.findAll();
    }

    public Optional<Lot> getLotById(int id) {
        return lotRepository.findById(id);
    }

    public Optional<Lot> getLotByNumero(String numeroLot) {
        return lotRepository.findByNumeroLot(numeroLot);
    }

    @Transactional
    public Lot saveLot(Lot lot) {
        return lotRepository.save(lot);
    }

    @Transactional
    public void deleteLot(int id) {
        lotRepository.deleteById(id);
    }
}
