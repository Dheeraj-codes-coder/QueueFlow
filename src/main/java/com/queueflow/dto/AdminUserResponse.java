package com.queueflow.dto;

import java.time.LocalDateTime;

public class AdminUserResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;

    public AdminUserResponse(
            Long userId,
            String fullName,
            String email,
            String phone,
            String role,
            boolean enabled,
            LocalDateTime createdAt) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}