
package com.queueflow.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.queueflow.dto.LoginRequest;
import com.queueflow.dto.LoginResponse;
import com.queueflow.entity.AppUser;
import com.queueflow.exception.InvalidCredentialsException;
import com.queueflow.repository.AppUserRepository;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthService(
            AppUserRepository appUserRepository,
            JwtService jwtService) {

        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        Optional<AppUser> optionalUser =
                appUserRepository.findByEmail(email);

        if (!optionalUser.isPresent()) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        AppUser user = optionalUser.get();

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                token,
                "Login successful"
        );
    }
}
