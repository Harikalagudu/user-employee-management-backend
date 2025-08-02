package com.example.user_employee_management_backend.dto;

/**
 * A simple, safe DTO to represent an Employee within other DTOs.
 */
public record SimpleEmployeeDto(Long id, String name, String email) {}
