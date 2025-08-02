package com.example.user_employee_management_backend.service;

import com.example.user_employee_management_backend.dto.DashboardStatsDto;
import com.example.user_employee_management_backend.model.EmployeeStatus;
import com.example.user_employee_management_backend.model.LeaveStatus; // <-- Import LeaveStatus
import com.example.user_employee_management_backend.repository.EmployeeRepository;
import com.example.user_employee_management_backend.repository.LeaveRequestRepository; // <-- Import LeaveRequestRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class DashboardService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // --- THIS IS THE FIX ---
    // 1. Inject the LeaveRequestRepository so the class knows what it is.
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;


    public DashboardStatsDto getDashboardStats() {
        // --- Get Employee Stats ---
        long totalEmployees = employeeRepository.count();

        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        long newThisMonth = employeeRepository.countByDateJoinedBetween(startOfMonth, endOfMonth);

        long activeEmployees = employeeRepository.countByStatus(EmployeeStatus.ACTIVE);
        long onProbationEmployees = employeeRepository.countByStatus(EmployeeStatus.ON_PROBATION);

        // --- THIS IS THE FIX ---
        // 2. Now the class can correctly call the repository method.
        long pendingLeaveRequests = leaveRequestRepository.countByStatus(LeaveStatus.PENDING);


        // 3. We must update the DTO to match this new data structure.
        return new DashboardStatsDto(
                totalEmployees,
                newThisMonth,
                pendingLeaveRequests, // <-- Use the new value
                activeEmployees,
                onProbationEmployees
        );
    }
}