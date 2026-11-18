
package com.queueflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientPrescriptionDashboardResponse;
import com.queueflow.repository.PatientProfileRepository;
import com.queueflow.service.PatientPrescriptionDashboardService;

@RestController
@RequestMapping("/api/patients")
public class PatientPrescriptionDashboardController {

    private final PatientPrescriptionDashboardService patientPrescriptionDashboardService;
    private final PatientProfileRepository patientProfileRepository;

    public PatientPrescriptionDashboardController(
            PatientPrescriptionDashboardService patientPrescriptionDashboardService,
            PatientProfileRepository patientProfileRepository) {

        this.patientPrescriptionDashboardService =
                patientPrescriptionDashboardService;

        this.patientProfileRepository = patientProfileRepository;
    }

    @GetMapping("/{patientId}/prescriptions/dashboard")
    public ResponseEntity<List<PatientPrescriptionDashboardResponse>>
            getPatientPrescriptions(@PathVariable Long patientId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean isOwner =
                patientProfileRepository.existsByIdAndUser_Email(
                        patientId, email);

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to access this patient's prescriptions");
        }

        List<PatientPrescriptionDashboardResponse> response =
                patientPrescriptionDashboardService
                        .getPatientPrescriptions(patientId);

        return ResponseEntity.ok(response);
    }
}
