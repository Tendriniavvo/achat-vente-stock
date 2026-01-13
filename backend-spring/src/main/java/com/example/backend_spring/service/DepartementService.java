package com.example.backend_spring.service;

import com.example.backend_spring.dto.DepartementDto;
import com.example.backend_spring.model.Departement;
import com.example.backend_spring.repository.DepartementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartementService {

    private final DepartementRepository departementRepository;

    public DepartementService(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

    public List<DepartementDto> getAllDepartements() {
        return departementRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<DepartementDto> getDepartementsActifs() {
        return departementRepository.findByActif(true).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DepartementDto getDepartementById(Integer id) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Département introuvable avec l'ID: " + id));
        return toDto(departement);
    }

    @Transactional
    public DepartementDto createDepartement(DepartementDto dto) {
        Departement departement = new Departement();
        departement.setCode(dto.getCode());
        departement.setNom(dto.getNom());
        departement.setDescription(dto.getDescription());
        departement.setActif(dto.getActif() != null ? dto.getActif() : true);

        Departement saved = departementRepository.save(departement);
        return toDto(saved);
    }

    @Transactional
    public DepartementDto updateDepartement(Integer id, DepartementDto dto) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Département introuvable avec l'ID: " + id));

        departement.setCode(dto.getCode());
        departement.setNom(dto.getNom());
        departement.setDescription(dto.getDescription());
        if (dto.getActif() != null) {
            departement.setActif(dto.getActif());
        }

        Departement updated = departementRepository.save(departement);
        return toDto(updated);
    }

    @Transactional
    public void deleteDepartement(Integer id) {
        if (!departementRepository.existsById(id)) {
            throw new RuntimeException("Département introuvable avec l'ID: " + id);
        }
        departementRepository.deleteById(id);
    }

    private DepartementDto toDto(Departement departement) {
        return new DepartementDto(
                departement.getId(),
                departement.getCode(),
                departement.getNom(),
                departement.getDescription(),
                departement.getActif());
    }
}
