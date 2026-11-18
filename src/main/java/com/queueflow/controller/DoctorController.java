
package com.queueflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.DoctorRegistrationRequest;
import com.queueflow.dto.DoctorRegistrationResponse;
import com.queueflow.entity.DoctorProfile;
import com.queueflow.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/register")
    public ResponseEntity<DoctorRegistrationResponse> registerDoctor(
            @Valid @RequestBody DoctorRegistrationRequest request) {

        DoctorProfile doctor = doctorService.registerDoctor(request);

        DoctorRegistrationResponse response = new DoctorRegistrationResponse(
                doctor.getId(),
                doctor.getClinicStaff().getUser().getId(),
                doctor.getClinicStaff().getUser().getFullName(),
                doctor.getClinicStaff().getUser().getEmail(),
                doctor.getClinicStaff().getClinic().getId(),
                doctor.getClinicStaff().getClinic().getName(),
                doctor.getQualification(),
                doctor.getExperienceYears(),
                doctor.getConsultationDurationMinutes(),
                doctor.getConsultationFee(),
                doctor.isActive()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
