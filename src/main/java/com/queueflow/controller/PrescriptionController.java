package com.queueflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientPrescriptionHistoryResponse;
import com.queueflow.dto.PrescriptionCreateRequest;
import com.queueflow.dto.PrescriptionDetailsResponse;
import com.queueflow.dto.PrescriptionResponse;
import com.queueflow.repository.ConsultationRepository;
import com.queueflow.repository.PatientProfileRepository;
import com.queueflow.repository.PrescriptionRepository;
import com.queueflow.service.PrescriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final PatientProfileRepository patientProfileRepository;

    public PrescriptionController(
            PrescriptionService prescriptionService,
            PrescriptionRepository prescriptionRepository,
            ConsultationRepository consultationRepository,
            PatientProfileRepository patientProfileRepository) {

        this.prescriptionService = prescriptionService;
        this.prescriptionRepository = prescriptionRepository;
        this.consultationRepository = consultationRepository;
        this.patientProfileRepository = patientProfileRepository;
    }

    private Authentication getAuthentication() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getEmail() {

        return getAuthentication().getName();
    }

    private boolean isDoctor() {

        return getAuthentication().getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_DOCTOR"));
    }

    private boolean isPatient() {

        return getAuthentication().getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_PATIENT"));
    }

    private void checkDoctorOwnership(Long consultationId) {

        boolean isOwner = consultationRepository
                .existsByIdAndDoctor_ClinicStaff_User_Email(
                        consultationId, getEmail());

        if (!isDoctor() || !isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to create a prescription for this consultation");
        }
    }

    private void checkPrescriptionOwnership(Long prescriptionId) {

        boolean isDoctorOwner = isDoctor()
                && prescriptionRepository
                        .existsByIdAndConsultation_Doctor_ClinicStaff_User_Email(
                                prescriptionId, getEmail());

        boolean isPatientOwner = isPatient()
                && prescriptionRepository
                        .existsByIdAndConsultation_QueueTicket_Patient_User_Email(
                                prescriptionId, getEmail());

        if (!isDoctorOwner && !isPatientOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to access this prescription");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @Valid @RequestBody PrescriptionCreateRequest request) {

        checkDoctorOwnership(request.getConsultationId());

        PrescriptionResponse response =
                prescriptionService.createPrescription(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{prescriptionId}")
    public ResponseEntity<PrescriptionDetailsResponse> getPrescriptionDetails(
            @PathVariable Long prescriptionId) {

        checkPrescriptionOwnership(prescriptionId);

        PrescriptionDetailsResponse response =
                prescriptionService.getPrescriptionDetails(prescriptionId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{patientId}/history")
    public ResponseEntity<List<PatientPrescriptionHistoryResponse>> getPatientPrescriptionHistory(
            @PathVariable Long patientId) {

        boolean isOwner = isPatient()
                && patientProfileRepository
                        .existsByIdAndUser_Email(patientId, getEmail());

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to access this patient's prescription history");
        }

        List<PatientPrescriptionHistoryResponse> response =
                prescriptionService.getPatientPrescriptionHistory(patientId);

        return ResponseEntity.ok(response);
    }
}