package com.queueflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientAppointmentResponse;
import com.queueflow.repository.PatientProfileRepository;
import com.queueflow.service.PatientAppointmentService;

@RestController
@RequestMapping("/api/patients")
public class PatientAppointmentController {

    private final PatientAppointmentService patientAppointmentService;
    private final PatientProfileRepository patientProfileRepository;

    public PatientAppointmentController(
            PatientAppointmentService patientAppointmentService,
            PatientProfileRepository patientProfileRepository) {

        this.patientAppointmentService = patientAppointmentService;
        this.patientProfileRepository = patientProfileRepository;
    }

    @GetMapping("/{patientId}/appointments")
    public ResponseEntity<List<PatientAppointmentResponse>> getPatientAppointments(
            @PathVariable Long patientId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        boolean isOwner = patientProfileRepository
                .existsByIdAndUser_Email(patientId, email);

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to access this patient's appointments");
        }

        List<PatientAppointmentResponse> appointments =
                patientAppointmentService.getPatientAppointments(patientId);

        return ResponseEntity.ok(appointments);
    }
}