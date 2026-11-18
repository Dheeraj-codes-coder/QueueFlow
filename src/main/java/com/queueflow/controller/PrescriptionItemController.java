package com.queueflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PrescriptionItemRequest;
import com.queueflow.dto.PrescriptionItemResponse;
import com.queueflow.repository.PrescriptionRepository;
import com.queueflow.service.PrescriptionItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionItemController {

    private final PrescriptionItemService prescriptionItemService;
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionItemController(
            PrescriptionItemService prescriptionItemService,
            PrescriptionRepository prescriptionRepository) {

        this.prescriptionItemService = prescriptionItemService;
        this.prescriptionRepository = prescriptionRepository;
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

    private void checkDoctorOwnership(Long prescriptionId) {

        boolean isOwner = isDoctor()
                && prescriptionRepository
                        .existsByIdAndConsultation_Doctor_ClinicStaff_User_Email(
                                prescriptionId, getEmail());

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to modify this prescription");
        }
    }

    private void checkViewOwnership(Long prescriptionId) {

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
                    "You are not allowed to view this prescription");
        }
    }

    @PostMapping("/{prescriptionId}/medicines")
    public ResponseEntity<PrescriptionItemResponse> addMedicine(
            @PathVariable Long prescriptionId,
            @Valid @RequestBody PrescriptionItemRequest request) {

        checkDoctorOwnership(prescriptionId);

        PrescriptionItemResponse response =
                prescriptionItemService.addMedicine(
                        prescriptionId, request);

        return new ResponseEntity<>(
                response, HttpStatus.CREATED);
    }

    @GetMapping("/{prescriptionId}/medicines")
    public ResponseEntity<List<PrescriptionItemResponse>> getPrescriptionMedicines(
            @PathVariable Long prescriptionId) {

        checkViewOwnership(prescriptionId);

        List<PrescriptionItemResponse> response =
                prescriptionItemService.getPrescriptionMedicines(
                        prescriptionId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prescriptionId}/medicines/{prescriptionItemId}")
    public ResponseEntity<PrescriptionItemResponse> updateMedicine(
            @PathVariable Long prescriptionId,
            @PathVariable Long prescriptionItemId,
            @Valid @RequestBody PrescriptionItemRequest request) {

        checkDoctorOwnership(prescriptionId);

        PrescriptionItemResponse response =
                prescriptionItemService.updateMedicine(
                        prescriptionId,
                        prescriptionItemId,
                        request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{prescriptionId}/medicines/{prescriptionItemId}")
    public ResponseEntity<String> deleteMedicine(
            @PathVariable Long prescriptionId,
            @PathVariable Long prescriptionItemId) {

        checkDoctorOwnership(prescriptionId);

        String response =
                prescriptionItemService.deleteMedicine(
                        prescriptionId,
                        prescriptionItemId);

        return ResponseEntity.ok(response);
    }
}