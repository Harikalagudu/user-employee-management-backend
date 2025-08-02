package com.example.user_employee_management_backend.dto;

public record DashboardStatsDto(
        long totalEmployees,
        long newThisMonth,
        // --- RENAME THIS FIELD to match the new data ---
        long pendingLeaveRequests,
        long activeEmployees,
        long onProbationEmployees1
) {}