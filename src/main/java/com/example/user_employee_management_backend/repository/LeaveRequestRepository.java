package com.example.user_employee_management_backend.repository;

import com.example.user_employee_management_backend.model.LeaveRequest;
import com.example.user_employee_management_backend.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Add this import
import org.springframework.data.repository.query.Param; // <-- Add this import
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // This method is for an employee's own history and is likely fine.
    List<LeaveRequest> findByEmployeeId(Long employeeId);

    // --- THIS IS THE DEFINITIVE FIX ---
    /**
     * Finds all leave requests with a given status using a custom JPQL query.
     * This query explicitly fetches the associated Employee and LeaveType entities
     * in a single database call to prevent performance issues and ensure data is loaded.
     * We explicitly compare the status enum.
     * @param status The status to search for (e.g., LeaveStatus.PENDING).
     * @return A List of LeaveRequest entities with their relationships pre-loaded.
     */
    @Query("SELECT lr FROM LeaveRequest lr JOIN FETCH lr.employee JOIN FETCH lr.leaveType WHERE lr.status = :status")
    List<LeaveRequest> findByStatusWithDetails(@Param("status") LeaveStatus status);
    long countByStatus(LeaveStatus status);
}