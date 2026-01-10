package com.example.backend_spring.service.security;

import com.example.backend_spring.model.Role;
import com.example.backend_spring.repository.security.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
    
    public Optional<Role> findById(Integer id) {
        return roleRepository.findById(id);
    }
    
    public Optional<Role> findByNom(String nom) {
        return roleRepository.findByNom(nom);
    }
}
