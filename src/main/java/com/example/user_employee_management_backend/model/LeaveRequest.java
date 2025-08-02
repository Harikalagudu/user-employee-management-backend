package com.example.user_employee_management_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit; // Import for date calculation

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    // --- ADDED FIELD FOR num_days ---
    @Column(name = "num_days", nullable = false) // Make sure this column is not nullable in DB
    private Long numDays;

    // --- Constructors ---
    public LeaveRequest() {
    }

    // You might have a constructor for creating new requests, ensure numDays is not explicitly set here
    // as it will be calculated by @PrePersist/@PreUpdate
    public LeaveRequest(Employee employee, LeaveType leaveType, LocalDate startDate, LocalDate endDate, String reason, LeaveStatus status) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        // numDays will be calculated automatically by the @PrePersist method
    }


    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    // --- ADDED Getter and Setter for num_days ---
    public Long getNumDays() {
        return numDays;
    }

    public void setNumDays(Long numDays) {
        this.numDays = numDays;
    }

    // --- ADDED Lifecycle Callbacks to calculate num_days ---
    // This method will be called automatically before persisting (saving) or updating the entity
    @PrePersist
    @PreUpdate
    public void calculateNumDays() {
        if (this.startDate != null && this.endDate != null) {
            // Calculate days between start and end date (inclusive)
            this.numDays = ChronoUnit.DAYS.between(this.startDate, this.endDate) + 1;
        } else {
            // If dates are not set, default to 0 days or handle as an error
            this.numDays = 0L;
        }
    }
}
