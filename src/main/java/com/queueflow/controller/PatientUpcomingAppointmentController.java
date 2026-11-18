
package com.queueflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientUpcomingAppointmentResponse;
import com.queueflow.service.PatientUpcomingAppointmentService;

@RestController
@RequestMapping("/api/patients")
public class PatientUpcomingAppointmentController {

    private final PatientUpcomingAppointmentService
            patientUpcomingAppointmentService;

    public PatientUpcomingAppointmentController(
            PatientUpcomingAppointmentService patientUpcomingAppointmentService) {

        this.patientUpcomingAppointmentService =
                patientUpcomingAppointmentService;
    }

    @GetMapping("/{patientId}/appointments/upcoming")
    public ResponseEntity<List<PatientUpcomingAppointmentResponse>>
            getUpcomingAppointments(
                    @PathVariable Long patientId) {

        List<PatientUpcomingAppointmentResponse> response =
                patientUpcomingAppointmentService
                        .getUpcomingAppointments(patientId);

        return ResponseEntity.ok(response);
    }
}
