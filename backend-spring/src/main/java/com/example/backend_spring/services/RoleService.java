package com.example.backend_spring.services;

import com.example.backend_spring.models.Role;
import com.example.backend_spring.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(int id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> getRoleByNom(String nom) {
        return roleRepository.findByNom(nom);
    }

    @Transactional
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(int id) {
        roleRepository.deleteById(id);
    }
}
