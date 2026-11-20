package com.queueflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.queueflow.entity.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findAllByOrderByCreatedAtDesc();

    long countByRoleAndEnabledTrue(String role);
}