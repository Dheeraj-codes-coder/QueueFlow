
package com.queueflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.ClinicRegistrationRequest;
import com.queueflow.entity.Clinic;
import com.queueflow.service.ClinicService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PostMapping("/register")
    public ResponseEntity<Clinic> registerClinic(
            @Valid @RequestBody ClinicRegistrationRequest request) {

        Clinic clinic = clinicService.registerClinic(request);

        return new ResponseEntity<>(clinic, HttpStatus.CREATED);
    }
}

