package com.example.backend_spring.services;

import com.example.backend_spring.models.Taxe;
import com.example.backend_spring.repositories.TaxeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaxeService {
    private final TaxeRepository taxeRepository;

    public List<Taxe> getAllTaxes() {
        return taxeRepository.findAll();
    }

    public Optional<Taxe> getTaxeById(int id) {
        return taxeRepository.findById(id);
    }

    @Transactional
    public Taxe saveTaxe(Taxe taxe) {
        return taxeRepository.save(taxe);
    }

    @Transactional
    public void deleteTaxe(int id) {
        taxeRepository.deleteById(id);
    }
}
