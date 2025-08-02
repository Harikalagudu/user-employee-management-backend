package com.example.user_employee_management_backend.service;

import com.example.user_employee_management_backend.dto.EmployeeOnboardRequestDto;
import com.example.user_employee_management_backend.model.*;
import com.example.user_employee_management_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private LeaveTypeRepository leaveTypeRepository;
    @Autowired private LeaveBalanceRepository leaveBalanceRepository;

    /**
     * A "Smart Onboarding" method that performs three actions in one transaction:
     * 1. Creates the new Employee record.
     * 2. Creates a corresponding User login account with ROLE_EMPLOYEE.
     * 3. Creates default leave balances for the new employee.
     * The @Transactional annotation ensures that if any step fails, all previous steps are rolled back.
     */
    @Transactional
    public Employee onboardEmployee(EmployeeOnboardRequestDto request) {
        // --- 1. Validate and Create the Employee Record ---
        if (employeeRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("An employee with this email already exists.");
        }

        Employee newEmployee = new Employee();
        newEmployee.setName(request.name());
        newEmployee.setEmail(request.email());
        newEmployee.setDepartment(request.department());
        // You may need to add age and experience back to your Employee model if needed
        // newEmployee.setAge(request.age());
        // newEmployee.setTotalExperience(request.totalExperience());
        newEmployee.setDateJoined(request.dateJoined());
        newEmployee.setDateOfBirth(request.dateOfBirth());
        newEmployee.setStatus(request.status());
        newEmployee.setProfileComplete(true);
        Employee savedEmployee = employeeRepository.save(newEmployee);

        // --- 2. Create the User Login Account ---
        String username = request.email(); // Use email as the username for simplicity and uniqueness
        if (userRepository.existsByUsername(username)) {
            // This is a failsafe, should be caught by the employee email check above.
            throw new IllegalArgumentException("A user account with this email already exists.");
        }
        User employeeUser = new User();
        employeeUser.setUsername(username);
        employeeUser.setEmail(request.email());
        employeeUser.setPassword(passwordEncoder.encode(request.temporaryPassword()));
        employeeUser.setRole(Role.ROLE_EMPLOYEE);
        employeeUser.setFirstTimeLogin(true); // Must reset password on first login
        employeeUser.setEnabled(true);
        userRepository.save(employeeUser);

        // --- 3. Create Default Leave Balances for the new Employee ---
        List<LeaveType> allLeaveTypes = leaveTypeRepository.findAll();
        for (LeaveType type : allLeaveTypes) {
            LeaveBalance newBalance = new LeaveBalance();
            newBalance.setEmployee(savedEmployee);
            newBalance.setLeaveType(type);
            newBalance.setRemainingDays(type.getDefaultDays());
            leaveBalanceRepository.save(newBalance);
        }

        return savedEmployee;
    }

    public Employee updateEmployee(Long id, EmployeeOnboardRequestDto request) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));

        // Update fields for the existing employee
        existingEmployee.setName(request.name());
        existingEmployee.setEmail(request.email());
        existingEmployee.setDepartment(request.department());
        existingEmployee.setDateJoined(request.dateJoined());
        existingEmployee.setDateOfBirth(request.dateOfBirth());
        existingEmployee.setStatus(request.status());

        return employeeRepository.save(existingEmployee);
    }

    public Page<Employee> getEmployees(Pageable pageable, String department, EmployeeStatus status) {
        Specification<Employee> spec = Specification.where(null);
        if (department != null && !department.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("department"), department));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        return employeeRepository.findAll(spec, pageable);
    }

    /**
     * Deletes an employee from the database by their ID.
     * Note: This does NOT currently delete the associated User account.
     * A more robust implementation would also handle User account deletion.
     */
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }
}