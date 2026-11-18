package com.queueflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.queueflow.dto.AppointmentBookingRequest;
import com.queueflow.dto.AppointmentBookingResponse;
import com.queueflow.dto.AppointmentCancellationResponse;
import com.queueflow.dto.AppointmentRescheduleRequest;
import com.queueflow.entity.Appointment;
import com.queueflow.entity.PatientProfile;
import com.queueflow.repository.PatientProfileRepository;
import com.queueflow.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientProfileRepository patientProfileRepository;

    public AppointmentController(
            AppointmentService appointmentService,
            PatientProfileRepository patientProfileRepository) {

        this.appointmentService = appointmentService;
        this.patientProfileRepository = patientProfileRepository;
    }

    private Long getLoggedInPatientId() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        PatientProfile patient = patientProfileRepository
                .findByUser_Email(email)
                .orElseThrow(() -> new AccessDeniedException(
                        "Patient profile not found"));

        return patient.getId();
    }

    private void checkPatientOwnership(
            Long requestedPatientId,
            Long loggedInPatientId) {

        if (!loggedInPatientId.equals(requestedPatientId)) {
            throw new AccessDeniedException(
                    "You are not allowed to access another patient's appointments");
        }
    }

    @PostMapping("/book")
    public ResponseEntity<AppointmentBookingResponse> bookAppointment(
            @Valid @RequestBody AppointmentBookingRequest request) {

        Long patientId = getLoggedInPatientId();

        checkPatientOwnership(request.getPatientId(), patientId);

        Appointment appointment = appointmentService.bookAppointment(
                patientId,
                request.getSlotId(),
                request.getReason()
        );

        AppointmentBookingResponse response = new AppointmentBookingResponse(
                appointment.getId(),
                appointment.getSlot().getId(),
                appointment.getStatus(),
                "Appointment booked successfully"
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentCancellationResponse> cancelAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long patientId) {

        Long loggedInPatientId = getLoggedInPatientId();

        checkPatientOwnership(patientId, loggedInPatientId);

        AppointmentCancellationResponse response =
                appointmentService.cancelAppointment(
                        loggedInPatientId, appointmentId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<AppointmentBookingResponse> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentRescheduleRequest request) {

        Long patientId = getLoggedInPatientId();

        checkPatientOwnership(request.getPatientId(), patientId);

        Appointment appointment = appointmentService.rescheduleAppointment(
                patientId,
                appointmentId,
                request.getNewSlotId()
        );

        AppointmentBookingResponse response = new AppointmentBookingResponse(
                appointment.getId(),
                appointment.getSlot().getId(),
                appointment.getStatus(),
                "Appointment rescheduled successfully"
        );

        return ResponseEntity.ok(response);
    }
}