package com.example.backend_spring.controllers;

import com.example.backend_spring.models.TypeEmplacement;
import com.example.backend_spring.repositories.TypeEmplacementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/types-emplacement")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TypeEmplacementController {

    private final TypeEmplacementRepository typeEmplacementRepository;

    @GetMapping
    public List<TypeEmplacement> getAllTypes() {
        return typeEmplacementRepository.findAll();
    }
}
