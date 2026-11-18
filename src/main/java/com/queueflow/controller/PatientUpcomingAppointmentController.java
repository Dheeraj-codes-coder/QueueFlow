package com.queueflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientUpcomingAppointmentResponse;
import com.queueflow.repository.PatientProfileRepository;
import com.queueflow.service.PatientUpcomingAppointmentService;

@RestController
@RequestMapping("/api/patients")
public class PatientUpcomingAppointmentController {

    private final PatientUpcomingAppointmentService patientUpcomingAppointmentService;
    private final PatientProfileRepository patientProfileRepository;

    public PatientUpcomingAppointmentController(
            PatientUpcomingAppointmentService patientUpcomingAppointmentService,
            PatientProfileRepository patientProfileRepository) {

        this.patientUpcomingAppointmentService =
                patientUpcomingAppointmentService;

        this.patientProfileRepository = patientProfileRepository;
    }

    @GetMapping("/{patientId}/appointments/upcoming")
    public ResponseEntity<List<PatientUpcomingAppointmentResponse>>
            getUpcomingAppointments(@PathVariable Long patientId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        boolean isOwner = patientProfileRepository
                .existsByIdAndUser_Email(patientId, email);

        if (!isOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to access this patient's appointments");
        }

        List<PatientUpcomingAppointmentResponse> response =
                patientUpcomingAppointmentService
                        .getUpcomingAppointments(patientId);

        return ResponseEntity.ok(response);
    }
}