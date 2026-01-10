package com.example.backend_spring.service.achat;

import com.example.backend_spring.repository.achat.ReceptionRepository;
import org.springframework.stereotype.Service;

@Service
public class ReceptionService {

    private final ReceptionRepository receptionRepository;

    public ReceptionService(ReceptionRepository receptionRepository) {
        this.receptionRepository = receptionRepository;
    }
}
