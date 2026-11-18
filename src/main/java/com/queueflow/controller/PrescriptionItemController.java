package com.queueflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.queueflow.service.PrescriptionItemService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionItemController {

    private final PrescriptionItemService prescriptionItemService;

    public PrescriptionItemController(
            PrescriptionItemService prescriptionItemService) {

        this.prescriptionItemService = prescriptionItemService;
    }

    @PostMapping("/{prescriptionId}/medicines")
    public ResponseEntity<PrescriptionItemResponse> addMedicine(
            @PathVariable Long prescriptionId,
            @Valid @RequestBody PrescriptionItemRequest request) {

        PrescriptionItemResponse response =
                prescriptionItemService.addMedicine(
                        prescriptionId,
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{prescriptionId}/medicines")
    public ResponseEntity<List<PrescriptionItemResponse>> getPrescriptionMedicines(
            @PathVariable Long prescriptionId) {

        List<PrescriptionItemResponse> response =
                prescriptionItemService.getPrescriptionMedicines(
                        prescriptionId
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{prescriptionId}/medicines/{prescriptionItemId}")
    public ResponseEntity<PrescriptionItemResponse> updateMedicine(
            @PathVariable Long prescriptionId,
            @PathVariable Long prescriptionItemId,
            @Valid @RequestBody PrescriptionItemRequest request) {

        PrescriptionItemResponse response =
                prescriptionItemService.updateMedicine(
                        prescriptionId,
                        prescriptionItemId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{prescriptionId}/medicines/{prescriptionItemId}")
    public ResponseEntity<String> deleteMedicine(
            @PathVariable Long prescriptionId,
            @PathVariable Long prescriptionItemId) {

        String response =
                prescriptionItemService.deleteMedicine(
                        prescriptionId,
                        prescriptionItemId
                );

        return ResponseEntity.ok(response);
    }
}