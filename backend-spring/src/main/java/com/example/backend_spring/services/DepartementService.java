package com.example.backend_spring.services;

import com.example.backend_spring.models.Departement;
import com.example.backend_spring.repositories.DepartementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartementService {
    private final DepartementRepository departementRepository;

    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    public Optional<Departement> getDepartementById(int id) {
        return departementRepository.findById(id);
    }

    @Transactional
    public Departement saveDepartement(Departement departement) {
        return departementRepository.save(departement);
    }

    @Transactional
    public void deleteDepartement(int id) {
        departementRepository.deleteById(id);
    }
}
