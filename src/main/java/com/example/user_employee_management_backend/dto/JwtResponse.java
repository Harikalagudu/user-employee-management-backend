// src/main/java/com/example/user_employee_management_backend/dto/JwtResponse.java
package com.example.user_employee_management_backend.dto;

import java.util.List;

public class JwtResponse {
    private String token;
    private String username;
    private List<String> roles; // THIS MUST BE List<String>
    private Boolean firstTimeLogin;

    public JwtResponse(String token, String username, List<String> roles, Boolean firstTimeLogin) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.firstTimeLogin = firstTimeLogin;
    }

    // Getters and Setters (ensure they exist)
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public Boolean getFirstTimeLogin() { return firstTimeLogin; }
    public void setFirstTimeLogin(Boolean firstTimeLogin) { this.firstTimeLogin = firstTimeLogin; }
}