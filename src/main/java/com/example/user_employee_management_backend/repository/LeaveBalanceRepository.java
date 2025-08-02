
package com.example.user_employee_management_backend.repository;

import com.example.user_employee_management_backend.model.Employee;
import com.example.user_employee_management_backend.model.LeaveBalance;
import com.example.user_employee_management_backend.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    List<LeaveBalance> findByEmployeeId(Long employeeId);
    Optional<LeaveBalance> findByEmployeeAndLeaveType(Employee employee, LeaveType leaveType);
}