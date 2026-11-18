package com.queueflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.queueflow.service.PrescriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(
            PrescriptionService prescriptionService) {

        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/create")
    public ResponseEntity<PrescriptionResponse> createPrescription(
            @Valid @RequestBody PrescriptionCreateRequest request) {

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

        PrescriptionDetailsResponse response =
                prescriptionService.getPrescriptionDetails(prescriptionId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{patientId}/history")
    public ResponseEntity<List<PatientPrescriptionHistoryResponse>> getPatientPrescriptionHistory(
            @PathVariable Long patientId) {

        List<PatientPrescriptionHistoryResponse> response =
                prescriptionService.getPatientPrescriptionHistory(patientId);

        return ResponseEntity.ok(response);
    }
}