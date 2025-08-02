package com.example.user_employee_management_backend.controller;

import com.example.user_employee_management_backend.dto.AdminDashboardStatsDto;
import com.example.user_employee_management_backend.dto.UserCreateRequest;
import com.example.user_employee_management_backend.dto.UserDto;
import com.example.user_employee_management_backend.payload.response.MessageResponse;
import com.example.user_employee_management_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Controller for all administrative actions.
 * Access is restricted to users with the 'ROLE_ADMIN'.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * Endpoint for an Admin to create a new user (HR or Manager).
     */
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        try {
            userService.createUser(userCreateRequest);
            return ResponseEntity.ok(new MessageResponse("User created successfully with role: " + userCreateRequest.role()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: A user with that username or email already exists."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An internal server error occurred while creating the user."));
        }
    }

    /**
     * Endpoint to fetch statistics for the Admin Dashboard homepage.
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(userService.getAdminDashboardStats());
    }

    /**
     * Endpoint to fetch a paginated list of all non-admin users.
     * Accessed using: /api/admin/users?page=0&size=5
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserDto> users = userService.getManageableUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint to enable or disable a user's account.
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> status) {
        Boolean isEnabled = status.get("enabled");
        if (isEnabled == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Request body must contain an 'enabled' field (true or false)."));
        }
        try {
            userService.setUserStatus(id, isEnabled);
            return ResponseEntity.ok(new MessageResponse("User status updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Endpoint to export all users to a CSV file.
     */
    @GetMapping("/users/export")
    public void exportUsersToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=users.csv");

        List<UserDto> users = userService.getManageableUsers(Pageable.unpaged()).getContent();

        PrintWriter writer = response.getWriter();
        writer.println("ID,Username,Email,Role,Enabled");

        for (UserDto user : users) {
            writer.println(String.format("%d,%s,%s,%s,%b",
                    user.id(),
                    user.username(),
                    user.email(),
                    user.role().name(),
                    user.enabled()));
        }

        writer.flush();
        writer.close();
    }
}
