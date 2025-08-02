package com.example.user_employee_management_backend.dto;

import com.example.user_employee_management_backend.model.Role;

public record UserDto(
        Long id,
        String username,
        String email,
        Role role,
        boolean enabled
) {}

