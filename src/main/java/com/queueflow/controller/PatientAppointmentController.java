
package com.queueflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.PatientAppointmentResponse;
import com.queueflow.service.PatientAppointmentService;

@RestController
@RequestMapping("/api/patients")
public class PatientAppointmentController {

    private final PatientAppointmentService patientAppointmentService;

    public PatientAppointmentController(
            PatientAppointmentService patientAppointmentService) {

        this.patientAppointmentService = patientAppointmentService;
    }

    @GetMapping("/{patientId}/appointments")
    public ResponseEntity<List<PatientAppointmentResponse>> getPatientAppointments(
            @PathVariable Long patientId) {

        List<PatientAppointmentResponse> appointments =
                patientAppointmentService.getPatientAppointments(patientId);

        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }
}
