package com.example.user_employee_management_backend.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a User entity in the database.
 * This class maps to the 'users' table and holds user credentials,
 * role, and status information.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Stores the enum as a string (e.g., "ROLE_ADMIN") for readability
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "boolean default true")
    private boolean firstTimeLogin = true;

    @Column(columnDefinition = "boolean default true")
    private boolean enabled = true; // Used to enable/disable user accounts by the Admin

    @Column(updatable = false) // This field should not be updated after creation
    private LocalDateTime createdAt;

    // This method is called by JPA before the entity is first saved to the database.
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // --- Getters and Setters for all fields ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
