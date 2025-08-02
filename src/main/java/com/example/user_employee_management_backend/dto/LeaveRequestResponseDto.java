package com.example.user_employee_management_backend.dto;

import com.example.user_employee_management_backend.model.LeaveRequest;
import com.example.user_employee_management_backend.model.LeaveStatus;
import java.time.LocalDate;

public record LeaveRequestResponseDto(
        Long id,
        String employeeName,
        String leaveTypeName,
        LocalDate startDate,
        LocalDate endDate,
        LeaveStatus status,
        String reason
) {
    /**
     * A "null-safe" factory method to convert a LeaveRequest entity to this DTO.
     * It provides default values if the nested employee or leaveType are null.
     */
    public static LeaveRequestResponseDto fromEntity(LeaveRequest leaveRequest) {
        // --- THIS IS THE FIX ---
        String employeeName = (leaveRequest.getEmployee() != null)
                ? leaveRequest.getEmployee().getName()
                : "DELETED EMPLOYEE";

        String leaveTypeName = (leaveRequest.getLeaveType() != null)
                ? leaveRequest.getLeaveType().getName()
                : "DELETED TYPE";

        return new LeaveRequestResponseDto(
                leaveRequest.getId(),
                employeeName,
                leaveTypeName,
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getStatus(),
                leaveRequest.getReason()
        );
    }
}