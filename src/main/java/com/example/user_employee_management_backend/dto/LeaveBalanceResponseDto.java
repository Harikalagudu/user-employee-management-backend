package com.example.user_employee_management_backend.dto;

import com.example.user_employee_management_backend.model.LeaveBalance;

public record LeaveBalanceResponseDto(
        Long id,
        Long leaveTypeId, // <-- ADD THIS FIELD
        String leaveTypeName,
        int remainingDays
) {
    public static LeaveBalanceResponseDto fromEntity(LeaveBalance leaveBalance) {
        return new LeaveBalanceResponseDto(
                leaveBalance.getId(),
                leaveBalance.getLeaveType().getId(), // <-- ADD THE ID HERE
                leaveBalance.getLeaveType().getName(),
                leaveBalance.getRemainingDays()
        );
    }
}