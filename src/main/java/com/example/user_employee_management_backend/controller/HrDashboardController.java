package com.example.user_employee_management_backend.controller;

import com.example.user_employee_management_backend.dto.DashboardStatsDto;
import com.example.user_employee_management_backend.dto.EmployeeOnboardRequestDto;
import com.example.user_employee_management_backend.dto.EmployeeSummaryDto;
import com.example.user_employee_management_backend.model.Employee;
import com.example.user_employee_management_backend.model.EmployeeStatus;
import com.example.user_employee_management_backend.service.DashboardService;
import com.example.user_employee_management_backend.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('HR', 'MANAGER')")
public class HrDashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/employees")
    public ResponseEntity<Page<EmployeeSummaryDto>> getEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) EmployeeStatus status,
            Pageable pageable) {

        // 1. Get the Page object of raw Employee entities from the service.
        Page<Employee> employeePage = employeeService.getEmployees(pageable, department, status);

        // 2. THIS IS THE FIX: Use the .map() function of the Page object to convert
        //    each Employee entity into an EmployeeSummaryDto.
        Page<EmployeeSummaryDto> dtoPage = employeePage.map(this::convertToSummaryDto);

        // 3. Return the new Page of DTOs. This is a stable, serializable object.
        return ResponseEntity.ok(dtoPage);
    }


    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeOnboardRequestDto employeeDto) {
        try {
            employeeService.onboardEmployee(employeeDto);
            return ResponseEntity.ok("Employee onboarded successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to onboard employee: " + e.getMessage());
        }
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody EmployeeOnboardRequestDto employeeDto) {
        try {
            employeeService.updateEmployee(id, employeeDto);
            return ResponseEntity.ok("Employee details updated successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update employee: " + e.getMessage());
        }
    }

    /**
     * Endpoint to delete an employee by their ID.
     * @param id The ID of the employee to delete, passed in the URL path.
     * @return A success or error response.
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete employee: " + e.getMessage());
        }
    }

    private EmployeeSummaryDto convertToSummaryDto(Employee employee) {
        if (employee == null) {
            return null;
        }
        return new EmployeeSummaryDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getStatus()
        );
    }
}