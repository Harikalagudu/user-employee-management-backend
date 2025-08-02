package com.example.user_employee_management_backend.service;

import com.example.user_employee_management_backend.dto.LeaveBalanceResponseDto;
import com.example.user_employee_management_backend.dto.LeaveRequestDto;
import com.example.user_employee_management_backend.dto.LeaveRequestResponseDto;
import com.example.user_employee_management_backend.model.*;
import com.example.user_employee_management_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true) // Set a default read-only transaction for all methods
public class LeaveService {

    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private LeaveTypeRepository leaveTypeRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public LeaveRequest submitLeaveRequest(String username, LeaveRequestDto requestDto) {
        if (requestDto.endDate().isBefore(requestDto.startDate())) {
            throw new IllegalArgumentException("Leave end date cannot be before the start date.");
        }
        Employee employee = findEmployeeByUsername(username);
        LeaveType leaveType = leaveTypeRepository.findById(requestDto.leaveTypeId())
                .orElseThrow(() -> new EntityNotFoundException("Leave Type not found"));

        // The num_days calculation will now be handled by the @PrePersist method in LeaveRequest entity.
        long daysRequested = ChronoUnit.DAYS.between(requestDto.startDate(), requestDto.endDate()) + 1; // Still needed for balance check

        LeaveBalance balance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType)
                .orElseThrow(() -> new IllegalStateException("No leave balance found for this leave type."));

        if (balance.getRemainingDays() < daysRequested) {
            throw new IllegalStateException("Insufficient leave balance.");
        }
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(requestDto.startDate());
        leaveRequest.setEndDate(requestDto.endDate());
        leaveRequest.setReason(requestDto.reason());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        // numDays is now automatically calculated by the @PrePersist method in the entity
        return leaveRequestRepository.save(leaveRequest);
    }

    /**
     * Retrieves all leave requests for an employee AND converts them to DTOs.
     * The return type is now List<LeaveRequestResponseDto>.
     */
    public List<LeaveRequestResponseDto> getMyLeaveRequests(String username) {
        Employee employee = findEmployeeByUsername(username);
        List<LeaveRequest> requests = leaveRequestRepository.findByEmployeeId(employee.getId());
        return requests.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all leave balances for an employee AND converts them to DTOs.
     * The return type is now List<LeaveBalanceResponseDto>.
     */
    public List<LeaveBalanceResponseDto> getMyLeaveBalances(String username) {
        Employee employee = findEmployeeByUsername(username);
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeId(employee.getId());
        return balances.stream()
                .map(LeaveBalanceResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all PENDING leave requests and converts them to DTOs for the manager.
     * It now calls the new, optimized repository method.
     */
    public List<LeaveRequestResponseDto> getPendingRequests() {
        List<LeaveRequest> pendingRequests = leaveRequestRepository.findByStatusWithDetails(LeaveStatus.PENDING);

        if (pendingRequests == null) {
            return Collections.emptyList();
        }
        return pendingRequests.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveRequest updateRequestStatus(Long requestId, LeaveStatus newStatus) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Leave Request not found with ID: " + requestId));

        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("This leave request has already been processed.");
        }

        // Only deduct days if the request is being approved
        if (newStatus == LeaveStatus.APPROVED) {
            // Use the numDays from the entity, which is calculated by @PrePersist/@PreUpdate
            long daysRequested = request.getNumDays(); // Use the already calculated numDays

            LeaveBalance balance = leaveBalanceRepository.findByEmployeeAndLeaveType(request.getEmployee(), request.getLeaveType())
                    .orElseThrow(() -> new IllegalStateException("No leave balance found for this employee and leave type."));

            if (balance.getRemainingDays() < daysRequested) {
                throw new IllegalStateException("Cannot approve. Employee has insufficient leave balance.");
            }

            balance.setRemainingDays(balance.getRemainingDays() - (int) daysRequested);
            leaveBalanceRepository.save(balance); // Save the updated balance
        }

        request.setStatus(newStatus);
        return leaveRequestRepository.save(request);
    }

    private Employee findEmployeeByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return employeeRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("No employee profile is associated with your user account."));
    }

    /**
     * Private helper method to convert a LeaveRequest entity to its DTO representation.
     */
    private LeaveRequestResponseDto convertToResponseDto(LeaveRequest leaveRequest) {
        String employeeName = (leaveRequest.getEmployee() != null) ? leaveRequest.getEmployee().getName() : "Unknown Employee";
        String leaveTypeName = (leaveRequest.getLeaveType() != null) ? leaveRequest.getLeaveType().getName() : "Unknown Type";

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
