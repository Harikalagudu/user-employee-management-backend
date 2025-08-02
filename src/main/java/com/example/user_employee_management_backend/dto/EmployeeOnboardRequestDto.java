package com.example.user_employee_management_backend.dto;

import com.example.user_employee_management_backend.model.EmployeeStatus;
import java.time.LocalDate;

/**
 * This record defines the "shape" of the incoming JSON for a new employee.
 * It is the contract between the frontend and the backend controller.
 */
public record EmployeeOnboardRequestDto(
        String name,
        String email,
        String department,
        int age,
        int totalExperience,
        String pastExperience,

        // --- CRITICAL ---
        // The name of this field, 'dateJoined', MUST EXACTLY MATCH the property
        // in the JSON payload sent from the React frontend.
        LocalDate dateJoined,

        LocalDate dateOfBirth,
        EmployeeStatus status,
        String temporaryPassword
) {}