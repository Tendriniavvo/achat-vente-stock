package com.example.backend_spring.repository.security;

import com.example.backend_spring.model.Permission;
import com.example.backend_spring.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    List<Permission> findByRole(Role role);

    List<Permission> findByModule(String module);

    List<Permission> findByRoleAndModule(Role role, String module);
}
