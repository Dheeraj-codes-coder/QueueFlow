package com.queueflow.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.AdminUserResponse;
import com.queueflow.entity.AppUser;
import com.queueflow.repository.AppUserRepository;

@Service
public class AdminUserService {

    private final AppUserRepository appUserRepository;

    public AdminUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers() {

        List<AppUser> users =
                appUserRepository.findAllByOrderByCreatedAtDesc();

        List<AdminUserResponse> responses = new ArrayList<>();

        for (AppUser user : users) {

            AdminUserResponse response = new AdminUserResponse(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    user.isEnabled(),
                    user.getCreatedAt()
            );

            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public AdminUserResponse updateUserStatus(
            Long userId,
            Boolean enabled,
            String loggedInEmail) {

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent admin from disabling their own account
        if (user.getEmail().equalsIgnoreCase(loggedInEmail)
                && !enabled) {

            throw new RuntimeException(
                    "You cannot deactivate your own admin account");
        }

        // Protect the last active admin account
        if ("ADMIN".equals(user.getRole())
                && !enabled
                && appUserRepository.countByRoleAndEnabledTrue("ADMIN") <= 1) {

            throw new RuntimeException(
                    "Cannot deactivate the last active admin account");
        }

        user.setEnabled(enabled);

        AppUser savedUser = appUserRepository.save(user);

        return new AdminUserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                savedUser.isEnabled(),
                savedUser.getCreatedAt()
        );
    }
}