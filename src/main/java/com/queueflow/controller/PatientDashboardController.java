package com.queueflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientDashboardResponse;
import com.queueflow.repository.PatientProfileRepository;
import com.queueflow.service.PatientDashboardService;

@RestController
@RequestMapping("/api/patients")
public class PatientDashboardController {

    private final PatientDashboardService patientDashboardService;
    private final PatientProfileRepository patientProfileRepository;

    public PatientDashboardController(
            PatientDashboardService patientDashboardService,
            PatientProfileRepository patientProfileRepository) {

        this.patientDashboardService = patientDashboardService;
        this.patientProfileRepository = patientProfileRepository;
    }

    @GetMapping("/{patientId}/dashboard")
    public ResponseEntity<PatientDashboardResponse> getPatientDashboard(
            @PathVariable Long patientId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        boolean isOwner = patientProfileRepository
                .existsByIdAndUser_Email(patientId, email);

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to access this patient's dashboard");
        }

        PatientDashboardResponse response =
                patientDashboardService.getPatientDashboard(patientId);

        return ResponseEntity.ok(response);
    }
}