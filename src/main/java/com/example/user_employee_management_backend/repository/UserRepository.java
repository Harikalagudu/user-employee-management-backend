package com.example.user_employee_management_backend.repository;

import com.example.user_employee_management_backend.model.User;
import com.example.user_employee_management_backend.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    long countByEnabled(boolean enabled);
    long countByRole(Role role);
    long countByRoleAndEnabled(Role role, boolean enabled);

    Page<User> findByRoleNot(Role role, Pageable pageable);

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
}
