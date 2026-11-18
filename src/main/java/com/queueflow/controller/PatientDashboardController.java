package com.queueflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientDashboardResponse;
import com.queueflow.service.PatientDashboardService;

@RestController
@RequestMapping("/api/patients")
public class PatientDashboardController {

    private final PatientDashboardService patientDashboardService;

    public PatientDashboardController(
            PatientDashboardService patientDashboardService) {

        this.patientDashboardService = patientDashboardService;
    }

    @GetMapping("/{patientId}/dashboard")
    public ResponseEntity<PatientDashboardResponse> getPatientDashboard(
            @PathVariable Long patientId) {

        PatientDashboardResponse response =
                patientDashboardService.getPatientDashboard(patientId);

        return ResponseEntity.ok(response);
    }
}