// src/main/java/com/example/user_employee_management_backend/dto/LoginRequest.java (Modified)
package com.example.user_employee_management_backend.dto;

import jakarta.validation.constraints.NotBlank; // Ensure this import exists

// MODIFIED: Added 'String role'
public record LoginRequest(@NotBlank String username, @NotBlank String password, @NotBlank String role) {
}