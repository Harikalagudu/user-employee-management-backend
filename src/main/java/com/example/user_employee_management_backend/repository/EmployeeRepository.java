package com.example.user_employee_management_backend.repository;

import com.example.user_employee_management_backend.model.Employee;
import com.example.user_employee_management_backend.model.EmployeeStatus; // Import the enum
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    long countByDateJoinedBetween(LocalDate start, LocalDate end);

    long countByProfileCompleteFalse();

    // Add this new method to get counts for specific statuses
    long countByStatus(EmployeeStatus status);
    // In EmployeeRepository.java
    Optional<Employee> findByEmail(String email);
}