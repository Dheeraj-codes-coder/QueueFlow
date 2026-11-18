
package com.queueflow.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.queueflow.dto.PatientRegistrationRequest;
import com.queueflow.entity.AppUser;
import com.queueflow.entity.PatientProfile;
import com.queueflow.repository.AppUserRepository;
import com.queueflow.repository.PatientProfileRepository;

@Service
public class PatientService {

    private final AppUserRepository appUserRepository;
    private final PatientProfileRepository patientProfileRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PatientService(
            AppUserRepository appUserRepository,
            PatientProfileRepository patientProfileRepository) {

        this.appUserRepository = appUserRepository;
        this.patientProfileRepository = patientProfileRepository;
    }

    @Transactional
    public PatientProfile registerPatient(PatientRegistrationRequest request) {

        // Check duplicate email

        String email = request.getEmail().trim().toLowerCase();

        if (appUserRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // Check duplicate phone

        String phone = request.getPhone().trim();

        if (appUserRepository.existsByPhone(phone)) {
            throw new RuntimeException("Phone number already registered");
        }

        // Create user

        AppUser user = new AppUser();

        user.setFullName(request.getFullName());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(phone);
        user.setRole("PATIENT");

        user = appUserRepository.save(user);

        // Create patient profile

        PatientProfile patient = new PatientProfile();

        patient.setUser(user);
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());

        return patientProfileRepository.save(patient);
    }
}
