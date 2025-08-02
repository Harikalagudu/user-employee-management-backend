package com.example.user_employee_management_backend.service;

import com.example.user_employee_management_backend.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserCreateRequest userCreateRequest);
    Page<UserDto> getManageableUsers(Pageable pageable);
    AdminDashboardStatsDto getAdminDashboardStats();
    void setUserStatus(Long id, Boolean isEnabled);
}

