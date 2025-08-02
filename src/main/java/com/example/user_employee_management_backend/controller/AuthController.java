// src/main/java/com/example/user_employee_management_backend/controller/AuthController.java (Modified)
package com.example.user_employee_management_backend.controller;

import com.example.user_employee_management_backend.dto.JwtResponse;
import com.example.user_employee_management_backend.dto.LoginRequest;
import com.example.user_employee_management_backend.dto.ResetPasswordRequest;
import com.example.user_employee_management_backend.payload.response.MessageResponse; // NEW IMPORT
import com.example.user_employee_management_backend.security.JwtUtils;
import com.example.user_employee_management_backend.security.UserDetailsImpl;
import com.example.user_employee_management_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // Ensure this import for @Valid
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600) // Ensure this matches your frontend origin
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate with the provided username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Get the actual role(s) from the authenticated user.
            // Since your User entity has a single `Role` field, userDetails will have one GrantedAuthority.
            List<String> userRoles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority) // E.g., "ROLE_ADMIN"
                    .collect(Collectors.toList());

            // Convert the requested role from frontend (e.g., "ADMIN") to Spring Security format (e.g., "ROLE_ADMIN")
            String requestedRoleFormatted = "ROLE_" + loginRequest.role().toUpperCase();

            // --- NEW ROLE VERIFICATION LOGIC ---
            // Check if the user actually possesses the role they selected on the login page
            if (!userRoles.contains(requestedRoleFormatted)) {
                // If not, return an error explaining the mismatch
                String actualRoleName = userRoles.isEmpty() ? "No Role" : userRoles.get(0).replace("ROLE_", "");
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Your account role is '"
                                + actualRoleName + "', but you tried to log in as '"
                                + loginRequest.role() + "'. Please select the correct role."));
            }

            // If authentication is successful AND the selected role matches, return JWT
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getUsername(),
                    userRoles, // Return the actual roles
                    userDetails.isFirstTimeLogin()));

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(401) // Unauthorized
                    .body(new MessageResponse("Error: Invalid username or password."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity
                    .status(401)
                    .body(new MessageResponse("Error: User not found."));
        } catch (Exception e) {
            // Catch any other unexpected exceptions during the process
            return ResponseEntity
                    .status(500)
                    .body(new MessageResponse("An unexpected error occurred during login: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        authService.resetPassword(username, resetPasswordRequest);
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully. Please log in again."));
    }
}