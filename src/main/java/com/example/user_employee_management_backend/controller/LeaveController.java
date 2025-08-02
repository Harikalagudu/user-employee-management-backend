package com.example.user_employee_management_backend.controller;

import com.example.user_employee_management_backend.dto.LeaveBalanceResponseDto;
import com.example.user_employee_management_backend.dto.LeaveRequestDto;
import com.example.user_employee_management_backend.dto.LeaveRequestResponseDto;
import com.example.user_employee_management_backend.model.LeaveStatus;
import com.example.user_employee_management_backend.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/requests")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<?> submitLeaveRequest(@RequestBody LeaveRequestDto requestDto) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            leaveService.submitLeaveRequest(username, requestDto);
            return ResponseEntity.ok("Leave request submitted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<List<LeaveRequestResponseDto>> getMyLeaveRequests() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(leaveService.getMyLeaveRequests(username));
    }

    @GetMapping("/my-balances")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<List<LeaveBalanceResponseDto>> getMyLeaveBalances() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(leaveService.getMyLeaveBalances(username));
    }

    @GetMapping("/requests/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveRequestResponseDto>> getPendingLeaveRequests() {
        return ResponseEntity.ok(leaveService.getPendingRequests());
    }

    @PutMapping("/requests/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateRequestStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            LeaveStatus newStatus = LeaveStatus.valueOf(body.get("status").toUpperCase());
            leaveService.updateRequestStatus(id, newStatus);
            return ResponseEntity.ok("Leave request status updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
