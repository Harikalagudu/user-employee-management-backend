package com.example.user_employee_management_backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record LeaveRequestDto(
        @NotNull(message = "Leave Type ID cannot be null")
        Long leaveTypeId,
        @NotNull(message = "Start date cannot be null")
        @FutureOrPresent(message = "Start date must be today or a future date")
        LocalDate startDate,
        @NotNull(message = "End date cannot be null")
        LocalDate endDate,
        @Size(max = 500, message = "Reason cannot be more than 500 characters")
        String reason
) {}