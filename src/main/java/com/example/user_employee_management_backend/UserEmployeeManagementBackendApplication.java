package com.example.user_employee_management_backend;

import com.example.user_employee_management_backend.model.*;
import com.example.user_employee_management_backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@SpringBootApplication
public class UserEmployeeManagementBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserEmployeeManagementBackendApplication.class, args);
		System.out.println("\n\n***** PAYFLOW APPLICATION HAS STARTED SUCCESSFULLY *****\n\n");
	}

	@Bean
	CommandLineRunner run(UserRepository userRepository,
						  EmployeeRepository employeeRepository,
						  LeaveTypeRepository leaveTypeRepository,
						  LeaveBalanceRepository leaveBalanceRepository,
						  PasswordEncoder passwordEncoder) {
		return args -> {
			System.out.println("***** STARTING DATABASE SEEDING PROCESS *****");

			// ... (All the logic for creating admin user and leave types) ...

			// Ensure all employees have leave balances
			List<LeaveType> allLeaveTypes = leaveTypeRepository.findAll();
			List<Employee> allEmployees = employeeRepository.findAll();
			for (Employee employee : allEmployees) {
				for (LeaveType leaveType : allLeaveTypes) {
					if (leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType).isEmpty()) {
						LeaveBalance newBalance = new LeaveBalance();
						newBalance.setEmployee(employee);
						newBalance.setLeaveType(leaveType);
						newBalance.setRemainingDays(leaveType.getDefaultDays());
						leaveBalanceRepository.save(newBalance);
						System.out.println("Created " + leaveType.getName() + " balance for employee: " + employee.getName());
					}
				}
			}
			System.out.println("***** DATABASE SEEDING PROCESS COMPLETE *****");
		};
	}
}