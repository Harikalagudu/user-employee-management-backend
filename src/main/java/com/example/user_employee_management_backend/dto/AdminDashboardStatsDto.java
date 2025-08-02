package com.example.user_employee_management_backend.dto;

public class AdminDashboardStatsDto {
    private final long totalHr;
    private final long totalManagers;
    private final long totalEmployees;
    private final long pendingOnboardings;
    private final long activeHr;
    private final long activeManagers;
    private final long activeEmployees;

    public AdminDashboardStatsDto(long totalHr, long totalManagers, long totalEmployees, long pendingOnboardings, long activeHr, long activeManagers, long activeEmployees) {
        this.totalHr = totalHr;
        this.totalManagers = totalManagers;
        this.totalEmployees = totalEmployees;
        this.pendingOnboardings = pendingOnboardings;
        this.activeHr = activeHr;
        this.activeManagers = activeManagers;
        this.activeEmployees = activeEmployees;
    }

    // Getters
    public long getTotalHr() {
        return totalHr;
    }

    public long getTotalManagers() {
        return totalManagers;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public long getPendingOnboardings() {
        return pendingOnboardings;
    }

    public long getActiveHr() {
        return activeHr;
    }

    public long getActiveManagers() {
        return activeManagers;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }
}

