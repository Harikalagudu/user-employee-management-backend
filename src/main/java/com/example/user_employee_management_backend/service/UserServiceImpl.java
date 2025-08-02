package com.example.user_employee_management_backend.service;

import com.example.user_employee_management_backend.dto.AdminDashboardStatsDto;
import com.example.user_employee_management_backend.dto.UserCreateRequest;
import com.example.user_employee_management_backend.dto.UserDto;
import com.example.user_employee_management_backend.model.Role;
import com.example.user_employee_management_backend.model.User;
import com.example.user_employee_management_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new user (HR or Manager only)
     */
    @Override
    public UserDto createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (request.email() != null && userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        Role role = request.role();

        if (role == Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Cannot create user with role ADMIN.");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        return new UserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.isEnabled()
        );
    }

    /**
     * Return paginated users excluding Admin
     */
    @Override
    public Page<UserDto> getManageableUsers(Pageable pageable) {
        Page<User> page = userRepository.findByRoleNot(Role.ROLE_ADMIN, pageable);
        return page.map(user -> new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled()
        ));
    }

    /**
     * Admin dashboard stats
     */
    @Override
    public AdminDashboardStatsDto getAdminDashboardStats() {
        long totalHr = userRepository.countByRole(Role.ROLE_HR);
        long activeHr = userRepository.countByRoleAndEnabled(Role.ROLE_HR, true);

        long totalManagers = userRepository.countByRole(Role.ROLE_MANAGER);
        long activeManagers = userRepository.countByRoleAndEnabled(Role.ROLE_MANAGER, true);

        long totalEmployees = userRepository.countByRole(Role.ROLE_EMPLOYEE);
        long activeEmployees = userRepository.countByRoleAndEnabled(Role.ROLE_EMPLOYEE, true);

        long pendingOnboardings = 5; // Replace with actual logic if needed

        return new AdminDashboardStatsDto(
                totalHr,
                activeHr,
                totalManagers,
                activeManagers,
                totalEmployees,
                activeEmployees,
                pendingOnboardings
        );
    }

    /**
     * Enable or disable a user
     */
    @Override
    public void setUserStatus(Long id, Boolean isEnabled) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }

        User user = optionalUser.get();
        user.setEnabled(isEnabled);
        userRepository.save(user);
    }
}
