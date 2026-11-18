
package com.queueflow.service;

import org.springframework.stereotype.Service;

import com.queueflow.dto.ClinicRegistrationRequest;
import com.queueflow.entity.Clinic;
import com.queueflow.repository.ClinicRepository;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public Clinic registerClinic(ClinicRegistrationRequest request) {

        // Check duplicate email

        if (request.getEmail() != null && !request.getEmail().isBlank()) {

            if (clinicRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Clinic email already registered");
            }
        }

        // Create clinic

        Clinic clinic = new Clinic();

        clinic.setName(request.getName());
        clinic.setAddress(request.getAddress());
        clinic.setPhones(request.getPhones());
        clinic.setEmail(request.getEmail());

        if (request.getTimezone() == null || request.getTimezone().isBlank()) {
            clinic.setTimezone("Asia/Kolkata");
        } else {
            clinic.setTimezone(request.getTimezone());
        }

        return clinicRepository.save(clinic);
    }
}
