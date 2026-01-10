package com.example.backend_spring.repository.security;

import com.example.backend_spring.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByNom(String nom);

    boolean existsByNom(String nom);
}
