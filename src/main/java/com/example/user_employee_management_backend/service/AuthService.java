package com.example.user_employee_management_backend.service;

import com.example.user_employee_management_backend.dto.ResetPasswordRequest;
import com.example.user_employee_management_backend.model.User;
import com.example.user_employee_management_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void resetPassword(String username, ResetPasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setFirstTimeLogin(false); // Update flag after password reset
        userRepository.save(user);
    }
}