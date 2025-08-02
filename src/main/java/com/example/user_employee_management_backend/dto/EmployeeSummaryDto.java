package com.example.user_employee_management_backend.dto;

import com.example.user_employee_management_backend.model.EmployeeStatus;

public record EmployeeSummaryDto(
        Long id,
        String name,
        String email,
        String department,
        EmployeeStatus status
) {}
