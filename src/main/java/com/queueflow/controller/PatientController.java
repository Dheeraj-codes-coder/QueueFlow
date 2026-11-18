
package com.queueflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientRegistrationRequest;
import com.queueflow.dto.PatientRegistrationResponse;
import com.queueflow.entity.PatientProfile;
import com.queueflow.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public ResponseEntity<PatientRegistrationResponse> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {

        PatientProfile patient = patientService.registerPatient(request);

        PatientRegistrationResponse response = new PatientRegistrationResponse(
                patient.getId(),
                patient.getUser().getId(),
                patient.getUser().getFullName(),
                patient.getUser().getEmail(),
                patient.getUser().getPhone(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getBloodGroup()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
