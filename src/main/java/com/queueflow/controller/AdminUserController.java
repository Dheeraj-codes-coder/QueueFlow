package com.queueflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.AdminUserResponse;
import com.queueflow.dto.AdminUserStatusRequest;
import com.queueflow.service.AdminUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {

        List<AdminUserResponse> users =
                adminUserService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserStatusRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        AdminUserResponse response =
                adminUserService.updateUserStatus(
                        userId,
                        request.getEnabled(),
                        email
                );

        return ResponseEntity.ok(response);
    }
}